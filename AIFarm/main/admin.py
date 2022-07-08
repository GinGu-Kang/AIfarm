from django.contrib import admin
from .models import *

admin.site.register(Profile)
admin.site.register(Tokens)
admin.site.register(Board)
admin.site.register(Comment)
admin.site.register(Like)
admin.site.register(HitCount)
admin.site.register(Message)
admin.site.register(MessageRoom)
admin.site.register(Notification)
admin.site.register(EmailAuthNumber)
