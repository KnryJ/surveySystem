drop database survey;
create database survey charset utf8mb4;
create table survey.users (
    uid int primary key auto_increment,
    username varchar(60) not null unique,
    password char(60) not null
) comment '系统用户，不包括填写调查问卷的人';

create table survey.questions (
    quid int primary key auto_increment,
    uid int not null comment '题目创建人',
    question varchar(200) not null comment '题目',
    options text not null comment '选项的 JSON 串，必须是数组'
) comment '题库中的题目，仅支持 4 个选项的单选题';

create table survey.surveys (
    suid int primary key auto_increment,
    uid int not null comment '调查问卷创建人',
    title varchar(200) not null comment '标题',
    brief varchar(400) not null comment '背景介绍'
) comment '未发布的调查问卷';

create table survey.survey_question_relations (
    sqrid int primary key auto_increment,
    suid int not null comment '调查问卷',
    quid int not null comment '题目',
    unique (suid, quid) comment '一份调查问卷不能出现相同的一道题'
) comment '调查问卷和题目的绑定关系';

create table survey.activities (
    acid int primary key auto_increment,
    uid int not null comment '发布人',
    suid int not null comment '发布的调查问卷',
    started_at datetime not null comment '开始时间',
    ended_at datetime not null comment '截至时间'
) comment '已发布的调查问卷';

create table survey.results (
    resid int primary key auto_increment,
    acid int not null comment '调查活动',
    nickname varchar(100) not null comment '被调查人称谓',
    phone varchar(20) not null comment '被调查人电话',
    gender int not null comment '被调查人性别 1 是 男；2 是 女',
    age int not null comment '被调查人年龄',
    answer text not null comment '被调查人的回答'
) comment '调查结果';