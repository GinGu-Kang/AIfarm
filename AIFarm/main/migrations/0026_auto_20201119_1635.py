# Generated by Django 2.2 on 2020-11-19 16:35

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0025_auto_20201119_1623'),
    ]

    operations = [
        migrations.AddField(
            model_name='notification',
            name='datetime',
            field=models.DateTimeField(auto_now_add=True, null=True),
        ),
        migrations.AlterField(
            model_name='hitcount',
            name='date',
            field=models.DateField(blank=True, default=datetime.datetime(2020, 11, 19, 16, 35, 29, 994200), null=True),
        ),
    ]