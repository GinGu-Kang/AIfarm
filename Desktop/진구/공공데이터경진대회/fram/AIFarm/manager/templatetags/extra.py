from django import template
from ..models import *
from bs4 import BeautifulSoup

register = template.Library()


@register.filter
def get_img_src(content):
    soup = BeautifulSoup(content,'html.parser')
    for img in soup.find_all('img'):
        return img.get('src')
    return None   