# Generated by Django 2.2 on 2020-11-20 21:51

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0027_auto_20201120_1319'),
    ]

    operations = [
        migrations.AlterField(
            model_name='board',
            name='category',
            field=models.CharField(blank=True, choices=[('1', '카페'), ('2', '음식점'), ('3', '공예'), ('4', 'IT'), ('5', '쇼핑몰'), ('6', '기타'), ('7', '자유게시판'), ('8', '중고물품 판매\x1d'), ('9', '마케팅'), ('10', '상권'), ('11', '매출'), ('12', '공모전'), ('13', '팀원모집'), ('14', '경제'), ('15', '기타'), ('16', '문의게시판')], max_length=1, null=True, verbose_name='카테고리'),
        ),
        migrations.AlterField(
            model_name='hitcount',
            name='date',
            field=models.DateField(blank=True, default=datetime.datetime(2020, 11, 20, 21, 51, 20, 467223), null=True),
        ),
    ]