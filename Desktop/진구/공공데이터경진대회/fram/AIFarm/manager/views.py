from django.shortcuts import render,redirect,get_object_or_404
from .models import *
from django.contrib.auth.models import User
from main.models import Profile
from django.contrib import auth 
from django.core.paginator import Paginator
from django.views.decorators.csrf import csrf_exempt

def page_nation(query_set,page):
    visible_page = 10
    paginator   = Paginator(query_set,10)
    query_set  = paginator.get_page(page)
    page_region = page//visible_page
    if page % visible_page != 0:
        page_region += 1
    start  = visible_page * page_region - (visible_page-1)
    end    =  start + visible_page - 1
    
    if query_set.paginator.num_pages < end:
        end = query_set.paginator.num_pages
    return query_set,start,end  
def login(request):
    context = {}
    if request.method == "POST":
        user =  auth.authenticate(request,username=request.POST['email'],password=request.POST['pw'])
        isAdmin = user.profile.isAdmin if user else None
        if isAdmin:
            auth.login(request,user)            
            return redirect('/manager/')
        context['msg'] = "로그인 실패 했습니다."
    return render(request,'login.html',context)    

def manage_main(request):
    if  request.user.is_authenticated:
        if not request.user.profile.isAdmin:
            return redirect("/manager/login/")
    else:
        return redirect("/manager/login/")
    

def write_notice(request):
    if request.user.is_authenticated:
        if not request.user.profile.isAdmin:
            return redirect("/manager/login/")
    else:
        return redirect("/manager/login/")        
    if request.method == "POST":
        Notice.objects.create(
            user    = request.user.profile,
            title   = request.POST.get('title'),
            content = request.POST.get('content')
        )
        return redirect("/manager/manageNotice/")

    return render(request,'writeNotice.html')

def manage_notice(request):
    if request.user.is_authenticated:
        if not request.user.profile.isAdmin:
            return redirect("/manager/login/")
    else:
        return redirect("/manager/login/")        
    notice_list = Notice.objects.all().order_by("-datetime")
    page = int(request.GET.get('page')) if request.GET.get('page') else 1
    notice_list,start,end = page_nation(notice_list,page)
    
    context = {
        'notice_list' : notice_list,
        'page_range'  : range(start,end+1),
        'next'        : end+1,
        'prev'        : start-1,
        'page'        : page,
    }
    return render(request,'manageNotice.html',context)

def detail_notice(request):
    if request.user.is_authenticated:
        if not request.user.profile.isAdmin:
            return redirect("/manager/login/")
    else:
        return redirect("/manager/login/")

    notice = Notice.objects.filter(id=request.GET.get('notice_id')).first()
    context = {
        'notice':notice,
    }        
    return render(request,'detail_notice.html',context)
@csrf_exempt
def delete_notice(request):
    if request.user.is_authenticated:
        if not request.user.profile.isAdmin:
            return redirect("/manager/login/")
    else:
        return redirect("/manager/login/")
    notice_id = request.GET.get('notice_id')
    if notice_id:
        notice = Notice.objects.filter(id=notice_id)
        notice.delete()
    else:
        notice = Notice.objects.filter(id__in=request.POST.getlist('check_list[]'))
        notice.delete()
    return redirect('/manager/manageNotice/')

def modify_notice(request):
    if request.user.is_authenticated:
        if not request.user.profile.isAdmin:
            return redirect("/manager/login/")
    else:
        return redirect("/manager/login/")
    notice = get_object_or_404(Notice,id=request.GET.get('notice_id'))
    if request.method == "POST":
        notice.title = request.POST.get('title') 
        notice.content = request.POST.get('content')
        notice.save()
        return redirect('/manager/detailNotice/?notice_id='+str(notice.id))
    context = {
        'notice':notice,
    }
    return render(request,'writeNotice.html',context)


def notice_list(request):
    notice_list = Notice.objects.all().order_by("-datetime")
    context = {
        'notice_list' : notice_list,
    }
    return render(request,'notice_list.html',context)

def moblie_detail_notice(request):
    notice = Notice.objects.filter(id=request.GET.get('notice_id')).first()
    context = {
        'notice':notice,
    }        
    return render(request,'mobileDetailNotice.html',context)

def write_info(request):
    if request.user.is_authenticated:
        if not request.user.profile.isAdmin:
            return redirect("/manager/login/")
    else:
        return redirect("/manager/login/")

    context = {}
    if request.method == "POST":
        Information.objects.create(
            poster_img   = request.FILES.get('poster_img'),
            homepage_url = request.POST.get('homepage_url')
        )
        context['info_msg'] = '작성완료'
    return render(request,'writeInfo.html',context)