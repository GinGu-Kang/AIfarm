from . import views
from django.urls import path
from django.conf import settings
from django.conf.urls.static import static
app_name="main"
urlpatterns = [
    path('test/',views.test,name="test"),
    path('image/',views.image,name="image"),
    #path('insect_2/',views.insect,name="insect"),
    path('weather/',views.weather,name="weather"),
    path('aidetail/', views.aiDetail, name="aidetail"),
    path('insect/detail/', views.detail, name="detail"),
    path('insect/detail_insect/', views.detail, name="detail_insect"),
    path('insect/',views.insect,name="insect"),
    path('gefo/',views.gefo,name="gefo"),
    path('register/',views.register,name="register"),
    path('emailCheck/', views.email_check,name="email_check"),
    path('login/',views.login, name="login"),
    path('main/<keyword>/',views.main, name="main"),
    #추가된내용
    path('newMain/', views.newMain, name="newMain"),
    path('sickness/', views.sickness, name="sickness"),
    path('camera/', views.camera, name="camera"),
    path('logout/',views.logout,name="logout"),
    path('writeBoard/',views.write_board,name="write_board"),
    path('modifyBoard/<id>/',views.modify_board),
    path('readBoard/<id>/',views.read_board),
    path('myBoard/<int:page>/',views.my_board),
    path('deleteBoard/',views.deleteBoard,name="deleteBoard"),
    path('fileUpload/',views.file_upload),
    path('writeComment/',views.write_comment),
    path('readRecomment/',views.read_recomment),
    path('like/<keyword>/',views.like),
    path('myComment/',views.my_commment),
    path('modifyComment/<commentId>/',views.modify_comment),
    path('deleteComment/',views.delete_comment),
    path('notification/',views.notification),
    path('deleteNotification/',views.deleteNotification),
    path('writeMeassage/',views.write_message),
    path('readMeassage/',views.read_meassage),
    path('messageChannel/',views.message_channel),
    path('deleteRoom/',views.delete_room),
    path('category/',views.category),
    path('boardList/',views.board_list),
    path('search/',views.search),
    path('modifyUser/',views.modify_user,name="modify_user"),
    path('checkAuthNumber/',views.check_auth_number,name="check_auth_number"),
    path('quetion/',views.quetion),
    path('programInfo/',views.program_info),
    path('form/<keyword>/',views.form),
    path('alarm/',views.alarm),
    path('settingAlarm/<keyword>/',views.setting_alarm),
    path('findPw/',views.find_pw)
    

]+ static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)