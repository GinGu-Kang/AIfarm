# Generated by Django 2.1.5 on 2021-08-11 11:48

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0037_auto_20210811_1142'),
    ]

    operations = [
        migrations.AlterField(
            model_name='hitcount',
            name='date',
            field=models.DateField(blank=True, default=datetime.datetime(2021, 8, 11, 11, 48, 25, 142891), null=True),
        ),
    ]
