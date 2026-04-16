package com.rabbiter.association.message;

import com.rabbiter.association.auth.AuthUser;
import com.rabbiter.association.message.dto.SendPrivateMessageRequest;
import com.rabbiter.association.msg.R;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageRestController {

    private final MessageFacade messageFacade;

    public MessageRestController(MessageFacade messageFacade) {
        this.messageFacade = messageFacade;
    }

    @GetMapping("/contacts")
    public R contacts(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(messageFacade.listContacts(authUser));
    }

    @GetMapping("/conversations/{contactId}")
    public R conversation(@PathVariable String contactId, Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return R.successData(messageFacade.getConversation(authUser, contactId));
    }

    @PostMapping("/conversations/{contactId}")
    public R send(@PathVariable String contactId,
                  @Validated @RequestBody SendPrivateMessageRequest request,
                  Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        messageFacade.sendMessage(authUser, contactId, request);
        return R.successMsg("私信发送成功");
    }
}
