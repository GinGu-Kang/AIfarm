# Generated by Django 2.2.7 on 2020-11-24 14:24

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0032_auto_20201124_1240'),
    ]

    operations = [
        migrations.AddField(
            model_name='profile',
            name='isCommentAlarm',
            field=models.BooleanField(default=True, verbose_name='댓글알림'),
        ),
        migrations.AddField(
            model_name='profile',
            name='isMessageAlarm',
            field=models.BooleanField(default=True, verbose_name='쪽지알림'),
        ),
        migrations.AddField(
            model_name='profile',
            name='isRecommentAlarm',
            field=models.BooleanField(default=True, verbose_name='답글알림'),
        ),
        migrations.AlterField(
            model_name='hitcount',
            name='date',
            field=models.DateField(blank=True, default=datetime.datetime(2020, 11, 24, 14, 24, 51, 300650), null=True),
        ),
    ]
