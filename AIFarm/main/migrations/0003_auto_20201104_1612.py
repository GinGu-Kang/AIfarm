# Generated by Django 2.2 on 2020-11-04 07:12

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0002_board'),
    ]

    operations = [
        migrations.AlterField(
            model_name='board',
            name='hits',
            field=models.IntegerField(blank=True, default=0, null=True, verbose_name='추천수'),
        ),
        migrations.AlterField(
            model_name='board',
            name='opposition',
            field=models.IntegerField(blank=True, default=0, null=True, verbose_name='반대'),
        ),
        migrations.AlterField(
            model_name='board',
            name='recommendation',
            field=models.IntegerField(blank=True, default=0, null=True, verbose_name='추천'),
        ),
    ]
