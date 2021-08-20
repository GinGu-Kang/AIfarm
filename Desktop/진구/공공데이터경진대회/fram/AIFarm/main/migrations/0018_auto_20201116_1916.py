# Generated by Django 2.2.7 on 2020-11-16 19:16

import datetime
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0017_auto_20201116_1844'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='message',
            name='receive_user',
        ),
        migrations.RemoveField(
            model_name='message',
            name='send_user',
        ),
        migrations.RemoveField(
            model_name='messageroom',
            name='relation',
        ),
        migrations.AddField(
            model_name='messageroom',
            name='receive_user',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='receiveuser', to='main.Profile'),
        ),
        migrations.AddField(
            model_name='messageroom',
            name='send_user',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='senduser', to='main.Profile'),
        ),
        migrations.AlterField(
            model_name='hitcount',
            name='date',
            field=models.DateField(blank=True, default=datetime.datetime(2020, 11, 16, 19, 16, 40, 164554), null=True),
        ),
    ]
