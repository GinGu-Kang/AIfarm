from django.db import models
from django.contrib.auth.models import User
from django.utils import timezone

class Profile(models.Model):
    GENDER = (
        ('1','남성'),
        ('2','여성')
    )
    CATEGORY  = (
        ('1' , '채소'),
        ('2' , '과일'),
        ('3' , '특수작물'),
        ('4' , '뿌리'),
        ('5' , '줄기'),
        ('6' , '기타'),
        ('7' , '자유게시판'),
        ('8' , '공유게시판'),
        ('9' , '정부지원'),
        ('10' , '주말농장'),
        ('11' , '작물판매'),
        ('12' , '공유농업일지'),
    )
    user       = models.OneToOneField(User,on_delete=models.CASCADE)
    category   = models.CharField(max_length=4,choices=CATEGORY,null=True,blank=True,verbose_name="관심분야")
    name       = models.CharField(max_length=20,verbose_name="이름")
    phone      = models.CharField(max_length=11,verbose_name="전화번호")
    birth_date = models.DateField(verbose_name="생년월일")
    gender     = models.CharField(max_length=1,choices=GENDER,verbose_name="성별")
    isAdmin    = models.BooleanField(default=False)
    isMessageAlarm = models.BooleanField(default=True,verbose_name="쪽지알림")
    isCommentAlarm = models.BooleanField(default=True,verbose_name="댓글알림")
    isRecommentAlarm = models.BooleanField(default=True,verbose_name="답글알림")
    

class EmailAuthNumber(models.Model):
    email       = models.CharField(max_length=100,verbose_name="이메일")
    auth_number = models.IntegerField()
    
class Tokens(models.Model):
    user  = models.ForeignKey(Profile,on_delete=models.CASCADE)
    token = models.CharField(max_length=200)

class Board(models.Model):
    CATEGORY  = (
        ('1' , '채소'),
        ('2' , '과일'),
        ('3' , '특수작물'),
        ('4' , '뿌리'),
        ('5' , '줄기'),
        ('6' , '기타'),
        ('7' , '자유게시판'),
        ('8' , '공유게시판'),
        ('9' , '정부지원'),
        ('10' , '주말농장'),
        ('11' , '작물판매'),
        ('12' , '공유농업일지'),
    )
    user      = models.ForeignKey(Profile,on_delete=models.CASCADE)
    title     = models.CharField(max_length=60,null=True,blank=True,verbose_name="제목")
    category  = models.CharField(max_length=4,choices=CATEGORY,null=True,blank=True,verbose_name="카테고리")
    content   = models.TextField(verbose_name="내용",null=True,blank=True)
    like_like = models.IntegerField(default=0,null=True,blank=True,verbose_name="좋아요수")
    hits      = models.IntegerField(default=0,null=True,blank=True,verbose_name="조회수")
    datetime  = models.DateTimeField(auto_now_add=True, verbose_name="작성시간")

class Comment(models.Model):
    user     = models.ForeignKey(Profile,on_delete=models.CASCADE)
    board    = models.ForeignKey(Board,null=True,blank=True,on_delete=models.CASCADE)
    like_cnt = models.IntegerField(default=0,verbose_name="좋아요수")
    content  = models.TextField(verbose_name="내용")
    datetime = models.DateTimeField(auto_now_add=True, verbose_name="작성시간")
    parent_id= models.IntegerField(default=0,null=True,blank=True,verbose_name="부모아이디")
    
    def __lt__(self, other):
        return self.id > other.id
class Like(models.Model):
    user     = models.ForeignKey(Profile,on_delete=models.CASCADE)
    board    = models.ForeignKey(Board,null=True,blank=True,on_delete=models.CASCADE)
    comment  = models.ForeignKey(Comment,null=True,blank=True,on_delete=models.CASCADE)

class HitCount(models.Model):
    ip    = models.CharField(max_length=15, blank=True, null=True)
    board = models.ForeignKey(Board, blank=True, null=True, on_delete=models.CASCADE)
    date  = models.DateField(default=timezone.now(), null=True, blank=True) 

class MessageRoom(models.Model):
    user1          = models.ForeignKey(Profile,on_delete=models.CASCADE,related_name="user1",blank=True, null=True)
    user2          = models.ForeignKey(Profile,on_delete=models.CASCADE,related_name="user2",blank=True, null=True)
    

class Message(models.Model):
    room             = models.ForeignKey(MessageRoom,on_delete=models.CASCADE,blank=True, null=True)
    send_user        = models.ForeignKey(Profile,on_delete=models.CASCADE,related_name="senduser",blank=True, null=True)
    receive_user     = models.ForeignKey(Profile,on_delete=models.CASCADE,related_name="receiveuser",blank=True, null=True)
    send_isDelete    = models.BooleanField(default=False)
    receive_isDelete = models.BooleanField(default=False)
    content          = models.TextField(null=True,blank=True)
    datetime         = models.DateTimeField(auto_now_add=True)
    def __lt__(self, other):
        return self.id > other.id

class Notification(models.Model):
    user = models.ForeignKey(Profile,on_delete=models.CASCADE,blank=True, null=True)
    comment = models.ForeignKey(Comment,on_delete=models.CASCADE,blank=True, null=True)
    message = models.ForeignKey(Message,on_delete=models.CASCADE,blank=True, null=True)
    datetime = models.DateTimeField(auto_now_add=True,blank=True, null=True)




    
    








