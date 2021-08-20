from . import views
from django.urls import path
from django.conf import settings
from django.conf.urls.static import static

app_name="manager"

urlpatterns = [
    path('',views.manage_main,name="manage_main"),
    path('writeNotice/',views.write_notice,name='write_notice'),
    path('login/',views.login, name="login"),
    path('manageNotice/',views.manage_notice,name="manage_notice"),
    path('detailNotice/',views.detail_notice,name="detail_notice"),
    path('deleteNotice/',views.delete_notice,name="delete_notice"),
    path('modifyNotice/',views.modify_notice,name="modify_notice"),
    path('noticeList/',views.notice_list,name="notice_list"),
    path('mobileDetailNotice/',views.moblie_detail_notice),
    path('writeInfo/',views.write_info,name="write_info")
]+ static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
