# Generated by Django 2.2 on 2020-11-21 11:06

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0029_auto_20201120_2201'),
    ]

    operations = [
        migrations.AddField(
            model_name='profile',
            name='isAdmin',
            field=models.BooleanField(default=False),
        ),
        migrations.AlterField(
            model_name='hitcount',
            name='date',
            field=models.DateField(blank=True, default=datetime.datetime(2020, 11, 21, 11, 6, 33, 370848), null=True),
        ),
    ]
