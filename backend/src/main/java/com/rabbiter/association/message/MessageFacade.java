package com.rabbiter.association.message;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbiter.association.auth.AuthRole;
import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.dao.MembersDao;
import com.rabbiter.association.dao.PrivateMessagesDao;
import com.rabbiter.association.dao.TeamsDao;
import com.rabbiter.association.dao.UsersDao;
import com.rabbiter.association.entity.Members;
import com.rabbiter.association.entity.PrivateMessage;
import com.rabbiter.association.entity.Teams;
import com.rabbiter.association.entity.Users;
import com.rabbiter.association.message.dto.MessageContactResponse;
import com.rabbiter.association.message.dto.PrivateMessageResponse;
import com.rabbiter.association.message.dto.SendPrivateMessageRequest;
import com.rabbiter.association.utils.DateUtils;
import com.rabbiter.association.utils.IDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class MessageFacade {

    private final PrivateMessagesDao privateMessagesDao;
    private final UsersDao usersDao;
    private final TeamsDao teamsDao;
    private final MembersDao membersDao;

    public MessageFacade(PrivateMessagesDao privateMessagesDao,
                         UsersDao usersDao,
                         TeamsDao teamsDao,
                         MembersDao membersDao) {
        this.privateMessagesDao = privateMessagesDao;
        this.usersDao = usersDao;
        this.teamsDao = teamsDao;
        this.membersDao = membersDao;
    }

    @Transactional(readOnly = true)
    public List<MessageContactResponse> listContacts(AuthUser authUser) {
        Map<String, ContactMeta> contactMap = new LinkedHashMap<>();
        AuthRole role = AuthRole.fromType(authUser.getType());

        if (role == AuthRole.SUPER_ADMIN) {
            for (Users user : usersDao.selectList(new QueryWrapper<>())) {
                if (!authUser.getUserId().equals(user.getId())) {
                    contactMap.put(user.getId(), new ContactMeta(user, "", ""));
                }
            }
        } else if (role == AuthRole.CLUB_ADMIN || role == AuthRole.TEACHER) {
            List<Teams> managedTeams = teamsDao.selectList(new QueryWrapper<Teams>().eq("manager", authUser.getUserId()));
            if (managedTeams.isEmpty()) {
                return buildContactResponses(contactMap, new HashMap<>(), authUser.getUserId());
            }
            Set<String> memberIds = membersDao.selectList(new QueryWrapper<Members>()
                            .in("team_id", managedTeams.stream().map(Teams::getId).collect(Collectors.toList())))
                    .stream()
                    .map(Members::getUserId)
                    .filter(userId -> !authUser.getUserId().equals(userId))
                    .collect(Collectors.toCollection(TreeSet::new));

            Map<String, Teams> teamById = managedTeams.stream().collect(Collectors.toMap(Teams::getId, team -> team));
            Map<String, List<Members>> membersByUser = membersDao.selectList(new QueryWrapper<Members>()
                            .in(!managedTeams.isEmpty(), "team_id", teamById.keySet()))
                    .stream()
                    .collect(Collectors.groupingBy(Members::getUserId));

            for (String memberId : memberIds) {
                Users user = usersDao.selectById(memberId);
                if (user == null) {
                    continue;
                }
                Teams sharedTeam = pickFirstManagedTeam(membersByUser.get(memberId), teamById);
                contactMap.put(memberId, new ContactMeta(user, sharedTeam == null ? "" : sharedTeam.getId(), sharedTeam == null ? "" : sharedTeam.getName()));
            }
        } else {
            List<Members> myMemberships = membersDao.selectList(new QueryWrapper<Members>().eq("user_id", authUser.getUserId()));
            if (!myMemberships.isEmpty()) {
                List<String> teamIds = myMemberships.stream().map(Members::getTeamId).collect(Collectors.toList());
                List<Teams> teams = teamsDao.selectBatchIds(teamIds);
                for (Teams team : teams) {
                    if (team == null || !StringUtils.hasText(team.getManager()) || authUser.getUserId().equals(team.getManager())) {
                        continue;
                    }
                    Users manager = usersDao.selectById(team.getManager());
                    if (manager != null) {
                        contactMap.putIfAbsent(manager.getId(), new ContactMeta(manager, team.getId(), team.getName()));
                    }
                }
            }
        }

        List<Map<String, Object>> rows = privateMessagesDao.listForUser(authUser.getUserId());
        for (Map<String, Object> row : rows) {
            String senderId = valueAsString(row.get("senderId"));
            String receiverId = valueAsString(row.get("receiverId"));
            String contactId = authUser.getUserId().equals(senderId) ? receiverId : senderId;
            if (!StringUtils.hasText(contactId) || authUser.getUserId().equals(contactId)) {
                continue;
            }
            if (!contactMap.containsKey(contactId)) {
                ContactMeta sharedMeta = resolveSharedMeta(authUser.getUserId(), contactId);
                if (sharedMeta == null) {
                    continue;
                }
                contactMap.put(contactId, sharedMeta);
            }
        }

        Map<String, ContactDigest> digests = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String senderId = valueAsString(row.get("senderId"));
            String receiverId = valueAsString(row.get("receiverId"));
            String contactId = authUser.getUserId().equals(senderId) ? receiverId : senderId;
            if (!contactMap.containsKey(contactId)) {
                continue;
            }

            ContactDigest digest = digests.computeIfAbsent(contactId, key -> new ContactDigest());
            if (!StringUtils.hasText(digest.lastMessageTime) || digest.lastMessageTime.compareTo(valueAsString(row.get("createTime"))) < 0) {
                digest.lastMessage = valueAsString(row.get("content"));
                digest.lastMessageTime = valueAsString(row.get("createTime"));
                if (!StringUtils.hasText(digest.clubName)) {
                    digest.clubName = valueAsString(row.get("teamName"));
                }
            }
            boolean unread = authUser.getUserId().equals(receiverId) && valueAsInteger(row.get("readStatus")) == 0;
            if (unread) {
                digest.unreadCount += 1;
            }
        }

        return buildContactResponses(contactMap, digests, authUser.getUserId());
    }

    @Transactional
    public List<PrivateMessageResponse> getConversation(AuthUser authUser, String contactId) {
        resolveAuthorizedClub(authUser.getUserId(), contactId);
        privateMessagesDao.markConversationRead(contactId, authUser.getUserId());
        return privateMessagesDao.listConversation(authUser.getUserId(), contactId).stream()
                .map(row -> new PrivateMessageResponse(
                        valueAsString(row.get("id")),
                        valueAsString(row.get("senderId")),
                        pickDisplayName(valueAsString(row.get("senderName")), valueAsString(row.get("senderUserName"))),
                        valueAsString(row.get("receiverId")),
                        pickDisplayName(valueAsString(row.get("receiverName")), valueAsString(row.get("receiverUserName"))),
                        valueAsString(row.get("teamId")),
                        valueAsString(row.get("teamName")),
                        valueAsString(row.get("content")),
                        valueAsString(row.get("createTime")),
                        authUser.getUserId().equals(valueAsString(row.get("senderId")))
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void sendMessage(AuthUser authUser, String contactId, SendPrivateMessageRequest request) {
        String teamId = resolveAuthorizedClub(authUser.getUserId(), contactId);
        PrivateMessage message = new PrivateMessage();
        message.setId(IDUtils.makeIDByCurrent());
        message.setSenderId(authUser.getUserId());
        message.setReceiverId(contactId);
        message.setTeamId(teamId);
        message.setContent(request.getContent());
        message.setCreateTime(DateUtils.getNowDate());
        message.setReadStatus(0);
        privateMessagesDao.insert(message);
    }

    private Teams pickFirstManagedTeam(List<Members> memberships, Map<String, Teams> teamById) {
        if (memberships == null) {
            return null;
        }
        for (Members membership : memberships) {
            Teams team = teamById.get(membership.getTeamId());
            if (team != null) {
                return team;
            }
        }
        return null;
    }

    private ContactMeta resolveSharedMeta(String currentUserId, String contactId) {
        Users contact = usersDao.selectById(contactId);
        if (contact == null) {
            return null;
        }
        String teamId = resolveAuthorizedClub(currentUserId, contactId);
        Teams team = StringUtils.hasText(teamId) ? teamsDao.selectById(teamId) : null;
        return new ContactMeta(contact, team == null ? "" : team.getId(), team == null ? "" : team.getName());
    }

    private String resolveAuthorizedClub(String currentUserId, String contactId) {
        Users current = usersDao.selectById(currentUserId);
        Users contact = usersDao.selectById(contactId);
        if (current == null || contact == null) {
            throw new IllegalArgumentException("私信对象不存在");
        }

        if (AuthRole.fromType(current.getType()) == AuthRole.SUPER_ADMIN) {
            return "";
        }

        List<Teams> teams = teamsDao.selectList(new QueryWrapper<>());
        List<Members> memberships = membersDao.selectList(new QueryWrapper<>());
        Map<String, Set<String>> teamMembers = memberships.stream()
                .collect(Collectors.groupingBy(Members::getTeamId,
                        Collectors.mapping(Members::getUserId, Collectors.toSet())));

        for (Teams team : teams) {
            Set<String> members = teamMembers.getOrDefault(team.getId(), Set.of());
            boolean currentInTeam = currentUserId.equals(team.getManager()) || members.contains(currentUserId);
            boolean contactInTeam = contactId.equals(team.getManager()) || members.contains(contactId);
            boolean oneIsManager = currentUserId.equals(team.getManager()) || contactId.equals(team.getManager());
            if (currentInTeam && contactInTeam && oneIsManager) {
                return team.getId();
            }
        }
        throw new IllegalArgumentException("当前仅支持社团管理者与本社成员之间私信");
    }

    private String displayName(Users user) {
        return StringUtils.hasText(user.getName()) ? user.getName() : user.getUserName();
    }

    private String pickDisplayName(String preferred, String fallback) {
        return StringUtils.hasText(preferred) ? preferred : fallback;
    }

    private String roleLabel(Integer type) {
        AuthRole role = AuthRole.fromType(type);
        if (role == AuthRole.SUPER_ADMIN) {
            return "系统管理员";
        }
        if (role == AuthRole.CLUB_ADMIN) {
            return "社团管理员";
        }
        if (role == AuthRole.TEACHER) {
            return "管理教师";
        }
        return "普通用户";
    }

    private String valueAsString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private List<MessageContactResponse> buildContactResponses(Map<String, ContactMeta> contactMap,
                                                               Map<String, ContactDigest> digests,
                                                               String currentUserId) {
        return contactMap.values().stream()
                .filter(meta -> !currentUserId.equals(meta.user.getId()))
                .map(meta -> {
                    ContactDigest digest = digests.getOrDefault(meta.user.getId(), new ContactDigest());
                    String clubId = StringUtils.hasText(meta.clubId) ? meta.clubId : "";
                    String clubName = StringUtils.hasText(digest.clubName) ? digest.clubName : meta.clubName;
                    return new MessageContactResponse(
                            meta.user.getId(),
                            meta.user.getUserName(),
                            displayName(meta.user),
                            roleLabel(meta.user.getType()),
                            clubId,
                            clubName,
                            digest.lastMessage,
                            digest.lastMessageTime,
                            digest.unreadCount
                    );
                })
                .sorted(Comparator.comparing(MessageContactResponse::getLastMessageTime, Comparator.nullsLast(String::compareTo)).reversed()
                        .thenComparing(MessageContactResponse::getDisplayName))
                .collect(Collectors.toList());
    }

    private Integer valueAsInteger(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (Objects.equals(value, true)) {
            return 1;
        }
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private static class ContactMeta {
        private final Users user;
        private final String clubId;
        private final String clubName;

        private ContactMeta(Users user, String clubId, String clubName) {
            this.user = user;
            this.clubId = clubId;
            this.clubName = clubName;
        }
    }

    private static class ContactDigest {
        private String lastMessage = "";
        private String lastMessageTime = "";
        private String clubName = "";
        private int unreadCount = 0;
    }
}
