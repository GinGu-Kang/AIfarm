from django.db import models
from main.models import Profile

class Notice(models.Model):
    user      = models.ForeignKey(Profile, on_delete=models.CASCADE)
    title     = models.CharField(max_length=200) #공지사항 제목
    content   = models.TextField() #공지사항 내용
    datetime  = models.DateTimeField(auto_now=True, verbose_name="작성시간")
    
class Information(models.Model):
    poster_img   =  models.ImageField(upload_to="info/")
    homepage_url =  models.CharField(max_length=300)
    datetime  = models.DateTimeField(auto_now=True, verbose_name="작성시간")
