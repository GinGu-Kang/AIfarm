from django import template
from ..models import *
from bs4 import BeautifulSoup

register = template.Library()

def get_object(model,**args):
    query_set = model.objects.filter(**args)
    return query_set[0] if query_set else None

@register.filter
def get_recomment_cnt(parent_id):
    return len(Comment.objects.filter(parent_id=parent_id))

@register.filter
def get_comment_cnt(board):
    return len(Comment.objects.filter(board=board))

@register.filter
def get_img_src(content):
    soup = BeautifulSoup(content,'html.parser')
    for img in soup.find_all('img'):
        return img.get('src')
    return None    

@register.filter
def isLogin(token, board_id):
    token = get_object(Tokens,token=token)
    board = get_object(Board,id=board_id)
    if token and board:
        return True if token.user == board.user else False
    return False   
@register.filter
def isLoginComment(token, comment_id):
    token = get_object(Tokens,token=token)
    comment = get_object(Comment,id=comment_id)
    print(token)
    print(comment)
    if token and comment:
        return True if token.user == comment.user else False
    return False       

@register.filter
def get_comment(parent_id):
    comment = get_object(Comment,id=parent_id)
    
    return comment.content
@register.filter
def get_parent_id(recomment_id):
    recomment = get_object(Comment,id=recomment_id)
    return recomment.parent_id if recomment else None

@register.filter
def get_parent_board(comment):
    if comment.board:
        return comment.board.id
    else:
        parent = get_object(Comment,id=comment.parent_id)
        return parent.board.id

#내가 아닌 아이디 찾기
@register.filter
def get_receive_id(room,token):
    token = get_object(Tokens,token=token)
    username = None
    if token:
        if token.user.id == room.user1.id:
            username = room.user2.user.username
        else:
            username = room.user1.user.username    
    return username
#상대방 닉네임
@register.filter
def get_receive_name(room,token):
    token = get_object(Tokens,token=token)
    name = None
    if token:
        if token.user.id == room.user1.id:
            name = room.user2.name
        else:
            name = room.user1.name   
    return name
@register.filter
def get_msg_receive_name(msg,token):
    token = get_object(Tokens,token=token)
    name = None
    if token:
        if token.user.id == msg.send_user.id:
            name = msg.receive_user.user.username
        else:
            name = msg.send_user.user.username
    return name
@register.filter
def isComment(q):
    return True if type(q) == Comment else False


    
