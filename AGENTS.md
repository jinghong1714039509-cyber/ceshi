# Local Project Notes

## Environment
- Backend default port: `8080`
- Frontend dev port: `5173`
- MySQL host: `localhost:3306`
- MySQL database: `association_manager`
- MySQL username: `root`
- MySQL password: `eent@123456`

## Startup
- Backend directory: `C:\Users\Administrator\Desktop\ceshi\backend`
- Frontend directory: `C:\Users\Administrator\Desktop\ceshi\frontend\app`
- Backend config file: `backend/src/main/resources/application.yml`

## Verification
- Health endpoint: `http://localhost:8080/api/public/health`
- Public API base: `http://localhost:8080/api/public`
- Frontend URL: `http://localhost:5173`

## Demo Data
- Seed script: `db/patches/002_social_space_seed.sql`
- Roles in use: `super-admin`, `club-admin`, `teacher-admin`, `student`
- Teacher accounts have the same club-management permissions as club managers

## Demo Accounts
- System admin: `superadmin / 123456`
- Teacher manager: `teacher_lin / 123456`
- Teacher manager: `teacher_qin / 123456`
- Club admin: `admin_media / 123456`
- Club admin: `admin_debate / 123456`
- Student: `student_chen / 123456`
- Student: `student_song / 123456`
- Student: `student_he / 123456`

## Demo Clubs
- `1760000001001` 星火机器人社
- `1760000001002` 山野影像社
- `1760000001003` 公共表达与辩论社
- `1760000001004` 城市公益行动社
- `1760000001005` 独立音乐实验室

## Key Authenticated Routes
- Clubs: `http://localhost:5173/clubs`
- Activities: `http://localhost:5173/activities`
- Notices: `http://localhost:5173/notices`
- Club space example: `http://localhost:5173/clubs/1760000001001/space`
