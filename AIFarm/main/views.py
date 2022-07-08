from django.shortcuts import render, redirect,get_object_or_404,HttpResponse
from django.contrib.auth.models import User
from .models import *
from manager.models import Information
import json
from django.views.decorators.csrf import csrf_exempt
from django.contrib import auth 
from django.core.files.storage import FileSystemStorage
from bs4 import BeautifulSoup
from .tokens import account_activation_token
from django.core.paginator import Paginator
from django.http import  Http404
from django.utils import timezone
from datetime import timedelta
from dateutil.relativedelta import relativedelta
import datetime
from django.db.models import Q 
from django.core.mail import EmailMessage
import random
from PIL import Image
import PIL
import torch
import torch.nn as nn
import numpy as np
import torchvision
from torchvision import datasets, models, transforms
import os
from django.views.decorators.csrf import csrf_exempt
from xml.etree.ElementTree import parse
from bs4 import BeautifulSoup
import requests


PATH='./data'
device = torch.device('cpu')
model= models.resnet18(pretrained=True)
num_ftrs = model.fc.in_features
model.fc = nn.Linear(num_ftrs, 19)
model.load_state_dict(torch.load(PATH+'model_state_dict.pt', map_location=device))
sickName=['노균병', '마일드모틀바이러스', '녹반모자이크바이러스', '노균병', '흰가루병', '점무늬병', '균핵병', '잿빛곰팡이병', '잎곰팡이병', '탄저병', '포도정상', '고추정상', '주키니호박정상', '애호박정상', '호박정상', '상추정상', '딸기정상', '토마토정상', '수박정상']
cropName=['포도', '없음', '없음', '참외', '호박', '없음', '상추', '딸기', '토마토', '수박', '포도정상', '고추정상', '주키니호박정상', '애호박정상', '호박정상', '상추정상', '딸기정상', '토마토정상', '수박정상']

def imff(imagedir):
    com=transforms.Compose([
        transforms.Resize(256),
        transforms.CenterCrop(224),
        transforms.ToTensor(),
        transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])
    
    #image=PIL.Image.open('./'+imagedir)
    image=imagedir
    image=com(image)
    inputs=image
    model.eval()
    inputs = inputs.to(device)
    inputs = inputs.unsqueeze(dim=0)
    outputs = model(inputs)
    print(outputs)
    _, preds = torch.max(outputs, 1)
    print(sickName[preds])

    return sickName[preds],cropName[preds]
    #print('predicted: {}'.format(class_names[preds]))

    
@csrf_exempt
def test(request):
    if request.method == "POST":
        sickName,cropName=imff(Image.open(request.FILES.get('image')))

        
    return HttpResponse(json.dumps({'sickName':sickName,"cropName":cropName}), content_type="application/json")

def gefo(request):
    if request.method=='GET':
        sickName=request.GET['sickName']
        cropName=request.GET['cropName']

        print(id)
        data={
            'sickName':sickName,
            'cropName':cropName
        }
    return render(request, 'test.html',data)

def aiDetail(request):
    if request.method=="GET":
        if request.GET.get('sickName')[-2:]!='정상':
            if request.GET.get('cropName')=="없음":
                data={'sickName':"질병을 찾지 못하였습니다..", 'src':"https://image.flaticon.com/icons/png/128/943/943591.png"}
                return render(request, 'test.html',data)
            else:
                sickNameKor = request.GET.get('sickName') #추후 request.GET or request.POST로 바꾸기
            
                cropName = request.GET.get('cropName') #추후 request.GET or request.POST로 바꾸기

                search_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC01&startPoint=1&displayCount=50&sickNameKor={sickNameKor}&cropName={cropName}"
                search_xml = requests.get(search_url)
                search_soup = BeautifulSoup(search_xml.text, "html.parser")
                #병코드 값 가져오기
                sickKey = search_soup.select_one('list > item sickkey').get_text()
                #이미지 가져오기
                thumbImg = search_soup.select_one('list > item thumbImg').get_text()

                detail_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC05&sickKey={sickKey}"
                detail_xml = requests.get(detail_url)
                detail_soup = BeautifulSoup(detail_xml.text, "html.parser")

                # thumbImg = request.GET.get("img")
                sickNameKor = detail_soup.select_one('sicknamekor').get_text()
                sickNameEng = detail_soup.select_one('sicknameeng').get_text()
                cropName = detail_soup.select_one('cropname').get_text()
                if detail_soup.select_one('virusList > item virusName') is None:
                    virusName=""
                else:
                    virusName = detail_soup.select_one('virusList > item virusName').get_text() #병원체명   


                developmentCondition = detail_soup.select_one('developmentCondition').get_text() #발생환경
                symptoms = detail_soup.select_one('symptoms').get_text() #증상설명
                preventionMethod = detail_soup.select_one('preventionMethod').get_text() #증상설명
                
                detail_data = {
                    "thumbImg" : thumbImg,
                    "sickNameKor" : sickNameKor,
                    "sickNameEng" : sickNameEng,
                    "cropName" : cropName,
                    "virusName" : virusName, #병원체명
                    "developmentCondition" : developmentCondition, #발생환경
                    "symptoms" : symptoms, #증상설명
                    "preventionMethod" : preventionMethod, #증상설명
                }
                return render(request, 'aidetail.html', detail_data)
        elif request.GET.get('sickName')[-2:]=="정상":
            data={'sickName':request.GET.get('sickName'),'src':'https://t3.ftcdn.net/jpg/02/26/35/22/240_F_226352250_XoFeQ0kxNX0PmnrRA1x8J0ieHzA7HuDe.jpg'}
            return render(request, 'test.html',data)
            
            
def image(request):
    return render(request, 'test.html')



# def gefo(request):
#     #초기화면 셋팅
#     korName = "갈색무늬병"
#     search_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC01&startPoint=1&displayCount=50&sickNameKor={korName}"
#     search_xml = requests.get(search_url)
#     search_soup = BeautifulSoup(search_xml.text, "html.parser")
#     list_data = []
#     for idx, item in enumerate(search_soup.select('list > item'), 1):
#         search_data = {
#             'cropName' : item.cropname.get_text(),
#             'sickNameKor': item.sicknamekor.get_text(),
#             'sickNameEng': item.sicknameeng.get_text(),
#             'thumbImg': item.thumbimg.get_text(),
#             'sickKey' : item.sickkey.get_text(),
#         }

#         list_data.append(search_data)
        
#     data={"data":list_data}
#     print(list_data)



#     return render(request, 'test.html',data)

#초기화면
def insect(request):
    #초기화면 셋팅
    if request.method=="GET":
        korName = "갈색무늬병"
        search_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC01&startPoint=1&displayCount=50&sickNameKor={korName}"
        search_xml = requests.get(search_url)
        search_soup = BeautifulSoup(search_xml.text, "html.parser")
        list_data = []
        for idx, item in enumerate(search_soup.select('list > item'), 1):
            search_data = {
                'cropName' : item.cropname.get_text(),
                'sickNameKor': item.sicknamekor.get_text(),
                'sickNameEng': item.sicknameeng.get_text(),
                'thumbImg': item.thumbimg.get_text(),
                'sickKey' : item.sickkey.get_text(),
                'search_type' : '병',
                }

            list_data.append(search_data)
        data={"data":list_data}
    else:
        if request.POST.get("type") == "병":
            korName = request.POST.get("disease")
            search_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC01&startPoint=1&displayCount=50&sickNameKor={korName}"
            search_xml = requests.get(search_url)
            search_soup = BeautifulSoup(search_xml.text, "html.parser")
            list_data = []
            for idx, item in enumerate(search_soup.select('list > item'), 1):
                search_data = {
                    'cropName' : item.cropname.get_text(),
                    'sickNameKor': item.sicknamekor.get_text(),
                    'sickNameEng': item.sicknameeng.get_text(),
                    'thumbImg': item.thumbimg.get_text(),
                    'sickKey' : item.sickkey.get_text(),
                    'search_type' : request.POST.get("type"),
                }

                list_data.append(search_data)
            data={"data":list_data}
        else: #해충일때
            korName = request.POST.get("disease")
            search_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC03&startPoint=1&displayCount=50&insectKorName={korName}"
            search_xml = requests.get(search_url)
            search_soup = BeautifulSoup(search_xml.text, "html.parser")
            list_data = []
            for idx, item in enumerate(search_soup.select('list > item'), 1):
                search_data = {
                    'cropName' : item.cropname.get_text(),
                    'sickNameKor': item.insectkorname.get_text(),
                    'sickNameEng': item.speciesname.get_text(),
                    'thumbImg': item.thumbimg.get_text(),
                    'sickKey' : item.insectkey.get_text(),
                    'search_type' : request.POST.get("type"),
                }

                list_data.append(search_data)
            data={"data":list_data}


    

    return render(request, 'insect.html',data)

#질병검색상세
def detail(request):
    if request.method =='GET':
        if request.GET.get("search_type") == "병":
            # print(request.GET.get("search_type"))
            print("="*50)
            print(request.GET.get("id"))
            print(request.GET.get("sickName"))
            print(request.GET.get('crop'))
            print(request.GET.get('search_type'))
            print("="*50)

            korName = request.GET.get('sickName')
            crop = request.GET.get('crop')
            img_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC01&startPoint=1&displayCount=50&sickNameKor={korName}&cropName={crop}"
            img_xml = requests.get(img_url)
            img_soup = BeautifulSoup(img_xml.text, "html.parser")

            thumbImg = img_soup.select_one('list > item thumbImg').get_text()
            print("="*50)
            print(thumbImg)
            print("="*50)
            sikKey = request.GET.get("id")
            detail_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC05&sickKey={sikKey}"
            detail_xml = requests.get(detail_url)
            detail_soup = BeautifulSoup(detail_xml.text, "html.parser")

            # thumbImg = request.GET.get("img")
            sickNameKor = detail_soup.select_one('sicknamekor').get_text()
            sickNameEng = detail_soup.select_one('sicknameeng').get_text()
            cropName = detail_soup.select_one('cropname').get_text()
            if detail_soup.select_one('virusList > item virusName') is None:
                virusName=""
            else:
                virusName = detail_soup.select_one('virusList > item virusName').get_text() #병원체명   
            
            
            developmentCondition = detail_soup.select_one('developmentCondition').get_text() #발생환경
            symptoms = detail_soup.select_one('symptoms').get_text() #증상설명
            preventionMethod = detail_soup.select_one('preventionMethod').get_text() #증상설명
            
            detail_data = {
                "thumbImg" : thumbImg,
                "sickNameKor" : sickNameKor,
                "sickNameEng" : sickNameEng,
                "cropName" : cropName,
                "virusName" : virusName, #병원체명
                "developmentCondition" : developmentCondition, #발생환경
                "symptoms" : symptoms, #증상설명
                "preventionMethod" : preventionMethod, #증상설명
            }
            return render(request, 'detail.html', detail_data)
        else:
            # print(request.GET.get("search_type"))
            print("="*50)
            print(request.GET.get("id"))
            print(request.GET.get("sickName"))
            print(request.GET.get('crop'))
            print(request.GET.get('search_type'))
            print("="*50)

            korName = request.GET.get('sickName')
            crop = request.GET.get('crop')
            img_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC03&startPoint=1&displayCount=50&insectKorName={korName}&cropName={crop}"
            img_xml = requests.get(img_url)
            img_soup = BeautifulSoup(img_xml.text, "html.parser")

            thumbImg = img_soup.select_one('list > item thumbImg').get_text()
            print("="*50)
            print(thumbImg)
            print("="*50)

            insectKey = request.GET.get("id")
            detail_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC07&insectKey={insectKey}"
            detail_xml = requests.get(detail_url)
            detail_soup = BeautifulSoup(detail_xml.text, "html.parser")

            insectSpeciesKor = detail_soup.select_one('insectSpeciesKor').get_text() #
            insectGenus = detail_soup.select_one('insectGenus').get_text() #
            insectOrder = detail_soup.select_one('insectOrder').get_text() #
            insectFamily = detail_soup.select_one('insectFamily').get_text() #
            authYear = detail_soup.select_one('authYear').get_text() #
            cropName = detail_soup.select_one('cropName').get_text() #
            stleInfo = detail_soup.select_one('stleInfo').get_text() #
            ecologyInfo = detail_soup.select_one('ecologyInfo').get_text() #
            damageInfo = detail_soup.select_one('damageInfo').get_text() #
            preventMethod = detail_soup.select_one('preventMethod').get_text() #
            
            detail_data = {
                "thumbImg" : thumbImg, #사진
                "insectSpeciesKor" : insectSpeciesKor,#한글명
                "insectGenus" : insectGenus,#속종명
                "insectOrder" : insectOrder,#목명
                "insectFamily" : insectFamily,#과명
                "authYear" : authYear, #명명년도
                "cropName" : cropName, #작물명
                "stleInfo" : stleInfo, #형태정보
                "ecologyInfo" : ecologyInfo, #생태정보
                "damageInfo" : damageInfo, #피해정보
                "preventMethod" : preventMethod, #방제방법
            }
    
            return render(request, 'detail_insect.html', detail_data)

    
#날씨
def weather(request):
    if request.method == "POST":
        date = request.POST.get('testDatepicker')
        area = request.POST.get('area')
        url = f'http://apis.data.go.kr/1390802/AgriWeather/WeatherObsrInfo/GnrlWeather/getWeatherTimeList?serviceKey=f8KpYUHNwWD39VEAJZ1K5TzO3NtBpPP7OA38IlElMdJPVETMKDj56XouoKnrQAzEqhjAvV8%2Bx%2B2%2FdWxbs4D4NQ%3D%3D&Page_No=1&Page_Size=20&date_Time={date}&obsr_Spot_Nm={area}'
        xml = requests.get(url)
        soup = BeautifulSoup(xml.text,"html.parser")

        area_list=[]
        for areas in soup.select('items > item'):
            area_list.append(areas.stn_name.get_text())
        print("=" *50)
        print(area_list)
        print("=" *50)


        newDate = request.POST.get('testDatepicker') + ' ' + request.POST.get('time')
        newArea = area_list[0]

        print(newArea)
        new_url = url = f'http://apis.data.go.kr/1390802/AgriWeather/WeatherObsrInfo/GnrlWeather/getWeatherTimeList?serviceKey=f8KpYUHNwWD39VEAJZ1K5TzO3NtBpPP7OA38IlElMdJPVETMKDj56XouoKnrQAzEqhjAvV8%2Bx%2B2%2FdWxbs4D4NQ%3D%3D&Page_No=1&Page_Size=20&date_Time={date}&obsr_Spot_Nm={newArea}'
        new_xml = requests.get(new_url)
        new_soup = BeautifulSoup(new_xml.text, "html.parser")

        print(new_soup)

        # stn_name = soup.select_one('items > item stn_name').get_text()
        # print(soup)
        print("=" * 50)
        for item in new_soup.select('items > item'):
            if item.date.get_text() == newDate:
                if item.temp is None:
                    continue
                else:
                    temp = item.temp.get_text()
                print(item.temp.get_text())
                if item.max_temp is None:
                    continue
                else:
                    max_temp = item.max_temp.get_text()
                print(item.max_temp.get_text())
                if item.min_temp is None:
                    continue
                else:
                    min_temp = item.min_temp.get_text()
                print(item.min_temp.get_text())
                if item.hum is None:
                    continue
                else:
                    hum = item.hum.get_text()
                print(item.hum.get_text())
                if item.widdir is None:
                    continue
                else:
                    widdir = item.widdir.get_text()
                print(item.widdir.get_text())
                if item.wind is None:
                    continue
                else:
                    wind = item.wind.get_text()
                print(item.wind.get_text())
                if item.rain is None:
                    continue
                else:
                    rain = item.rain.get_text()
                print(item.rain.get_text())
                if item.soil_wt is None:
                    continue
                else:
                    soil_wt = item.soil_wt.get_text()
                print(item.soil_wt.get_text())

                max_temp = item.max_temp.get_text()
                print(item.max_temp.get_text())
                min_temp = item.min_temp.get_text()
                print(item.min_temp.get_text())
                hum = item.hum.get_text()
                print(item.hum.get_text())
                widdir = item.widdir.get_text()
                print(item.widdir.get_text())
                wind = item.wind.get_text()
                print(item.wind.get_text())
                rain = item.rain.get_text()
                print(item.rain.get_text())
                soil_wt = item.soil_wt.get_text()
                print(item.soil_wt.get_text())

        #지도 사진 맵핑
        kkd_list=[
            "가평군 가평읍","고양시 구산동","고양시 덕양구","광주시 목현동","김포시 월곶면",'남양주 진건읍','수원시 서둔동','시흥시 하중동',"안산시 단원구",
            "안산시 상록구",'안성시 보개면','양주시 광적면','양평군 양평읍','여주시 상거동','연천군 신서면','연천군 연천읍','용인시 처인구','이천시 장호원',
            '이천시 중리동','이천시 진암리','파주시 아동동','평택시 오성면','포천시 신북면','포천시 영북면','화성시 마도면','화성시 장안면'
        ]
        kwd_list=[
            '강릉 안반덕이','동해시 북평동','삼척시 근덕면','삼척시 미로면','속초시 대포동','양구군 만대리','양구군 후리','양양군 손양면','영월군 영월읍',
            '원주시 흥업면','인제군 인제읍','정선군 신동읍','정선군 임계면','정선군 정선읍','철원군 동송읍','춘천시 신북읍','태백 귀네미골','태백시 매봉산',
            '태백시 황지동','평창군 여만리','평창군 운교리','평창군 진부면','홍천군 자운리','홍천군 화동리','화천군 화천읍','횡성군 공근면'
        ]
        cb_list=[
            '영동군 심천면','옥천군 옥천읍','음성군 소이면','제천시 봉양읍','진천군 진천읍','청원군 오창읍','청주시 남일면','충주시 달천동','충주시 안림동'
        ]
        cn_list=[
            '계룡시 두마면','공주시 우성면','금산군 금성면','논산시 광석면','당진시 당진읍','보령시 주교면','부여군 규암면','아산시 염치읍','예산군 신암면',
            '천안시 목천읍','천안시 성환읍','천안시 직산읍','청양군 청양읍','태안군 태안읍','홍성군 홍성읍'
        ]
        jb_list=[
            '군산시 개정면','김제시 부량면','남원시 운봉읍','남원시 이백면','무주군 무주읍','부안군 계화면','순창군 구림면','완주군 고산면','완주군 반교리',
            '완주군 이서면','익산시 함열읍','임실군 신평면','장수군 개정리','장수군 장수읍','정읍시 정우면','진안군 진안읍'
        ]
        jn_list=[
            '강진군 군동면','고흥군 풍양면','곡성군 오곡면',
            '구례군 구례읍',
            '나주시 공산면',
            '나주시 금천면',
            '나주시 문평면',
            '나주시 봉황면',
            '나주시 신포면',
            '무안군 청계면',
            '무안군 현경면',
            '보성군 보성읍',
            '보성군 웅치면',
            '순천시 주암면',
            '신안군 압해읍',
            '여수시 주삼동',
            '영광군 군서면',
            '영암군 덕진면',
            '완도군 완도읍',
            '장성군 장성읍',
            '장흥군 장흥읍',
            '진도군 군내면',
            "함평군 학교면",
            '해남군 삼산면',
            '해남군 옥천면',
            '화순군 한천면'
        ]
        kb_list=[
            '경산시 자인면',
            '경주시 용강상리',
            '구미시 선산읍',
            '구미시 옥성면',
            '군위군 소보면',
            '군위군 효령면',
            '대구시 북구',
            '문경시 흥덕동',
            '봉화군 봉성면',
            '봉화군 석포면',
            '봉화군 외삼리',
            "상주시 공성면",
            '상주시 외서면',
            '상주시 초산동',
            '상주시 화서면',
            '성주군 대가면',
            '성주군 대천리',
            '안동시 북후면',
            '안동시 송천동',
            '영덕군 병곡면',
            '영양군 대천리',
            '영양군 영양읍',
            '영주시 봉현면',
            '영주시 부석면',
            '영주시 안정로',
            '영주시 안풍리',
            '영주시 용산리',
            '영주시 평은면',
            '영주시 풍기읍',
            '영천시 금호읍',
            '영천시 오미동',
            '예천군 예천읍',
            '울진군 매화면',
            '의성군 봉양면',
            '의성군 옥산면',
            '의성군 의성읍',
            '청도군 각북면',
            '청도군 구라리',
            '청도군 이서면',
            '청도군 화양읍',
            '청송군 부남면',
            '청송군 청송읍',
            '칠곡군 약목면',
            '포항시 북구'
        ]
        kn_list=[
            '거제시 거제면',
            '거창군 거창읍',
            '거창군 동변리',
            '거창군 주상면',
            '고성군 고성읍',
            '김해시 전하동',
            '남해군 이동면',
            '밀양시 산내면',
            '밀양시 상남면',
            '사천시 송포동',
            '사천시 용현면',
            '진주시 초전동',
            '창녕군 대지면',
            '창원시 대산면',
            '통영시 광도면',
            '하동군 적량면',
            '합천군 용주면'
        ]
        jeju_list=[
            '서귀포 감산리',
            '서귀포 남원읍',
            '서귀포 대정읍',
            '서귀포 덕수리',
            '서귀포 성산읍',
            '서귀포 신효동',
            '서귀포 신흥리',
            '서귀포 중문동',
            '서귀포 창천리',
            '서귀포 표선면',
            '서귀포 하례리',
            '서귀포 하원동',
            '제주시 구좌읍',
            '제주시 금악리',
            '제주시 김녕리',
            '제주시 덕천리',
            '제주시 상귀길',
            '제주시 세화리',
            '제주시 신엄리',
            '제주시 애월읍',
            '제주시 오등동',
            '제주시 월각로',
            '제주시 조천읍',
            '제주시 한동리',
            '제주시 한림읍'
        ]
        sejong_list=['세종시 연서면']
        seoul_list=['서울시 서초구']
        busan_list=['부산시 강서구']

        if newArea in kkd_list:
            map_img = "../static/img/kkd.png"
        elif newArea in kwd_list:
            map_img = "../static/img/kwd.png"
        elif newArea in cb_list:
            map_img = "../static/img/cb.png"
        elif newArea in cn_list:
            map_img = "../static/img/cn.png"
        elif newArea in jb_list:
            map_img = "../static/img/jb.png"
        elif newArea in jn_list:
            map_img = "../static/img/jn.png"
        elif newArea in kb_list:
            map_img = "../static/img/kb.png"
        elif newArea in kn_list:
            map_img = "../static/img/kn.png"
        elif newArea in jeju_list:
            map_img = "../static/img/jeju.png"
        elif newArea in sejong_list:
            map_img = "../static/img/sejong.png"
        elif newArea in seoul_list:
            map_img = "../static/img/seoul.png"
        elif newArea in busan_list:
            map_img = "../static/img/busan.png"

        context= {
            "map_img" : map_img, #지역사진
            "area" : area, #지역명
            "wholeDate" : newDate, #날짜 및 시간
            "temp" : temp, #기온
            "max_temp" : max_temp, #최고기온
            "min_temp" : min_temp, #최저기온
            "hum" : hum, #습도
            "widdir" : widdir, #풍향
            "wind" : wind, #풍속
            "rain" : rain, #강수량
            "soil_wt" : soil_wt, #토양수분보정값
        }
        return render(request, 'weather.html', context)

    return render(request, 'weather.html')

"""def insect(request):
    #초기화면 셋팅
    if request.method=="GET":
        korName = "갈색무늬병"
        search_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC01&startPoint=1&displayCount=50&sickNameKor={korName}"
        search_xml = requests.get(search_url)
        search_soup = BeautifulSoup(search_xml.text, "html.parser")
        list_data = []
        for idx, item in enumerate(search_soup.select('list > item'), 1):
            search_data = {
                'cropName' : item.cropname.get_text(),
                'sickNameKor': item.sicknamekor.get_text(),
                'sickNameEng': item.sicknameeng.get_text(),
                'thumbImg': item.thumbimg.get_text(),
                'sickKey' : item.sickkey.get_text(),
            }

            list_data.append(search_data)
        
            
        data={"data":list_data}
    else:
        #print(request.POST.get("disease"),"ASASASASasASADASDASD@@@@@@@@@@")
        
        korName = request.POST.get("disease")
        search_url = f"https://ncpms.rda.go.kr/npmsAPI/api/pred_ajax_local_callback.jsp?apiKey=2021acbcf504f10f54fcce90afec0040ce5e&serviceCode=SVC01&startPoint=1&displayCount=50&sickNameKor={korName}"
        search_xml = requests.get(search_url)
        search_soup = BeautifulSoup(search_xml.text, "html.parser")
        list_data = []
        for idx, item in enumerate(search_soup.select('list > item'), 1):
            search_data = {
                'cropName' : item.cropname.get_text(),
                'sickNameKor': item.sicknamekor.get_text(),
                'sickNameEng': item.sicknameeng.get_text(),
                'thumbImg': item.thumbimg.get_text(),
                'sickKey' : item.sickkey.get_text(),
            }

            list_data.append(search_data)
        
            
        data={"data":list_data}
    return render(request, 'insect.html',data)"""






def get_msg_receive_name(msg,token):
    token = get_object(Tokens,token=token)
    name = None
    if token:
        if token.user.id == msg.send_user.id:
            name = msg.receive_user.user.username
        else:
            name = msg.send_user.user.username
    return name
def get_object(model,**args):
    query_set = model.objects.filter(**args)
    return query_set[0] if query_set else None

def page_nation(query_set,page):
    visible_page = 5
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

@csrf_exempt
def newMain(request):
    return render(request, 'newMain.html')

def sickness(request):


    return render(request, 'sickness.html')

def camera(request):
    return render(request, 'camera.html')

@csrf_exempt
def main(request,keyword=None):
    date = datetime.datetime.now()
    boar_list = None
    active_index = 0
    token = request.POST.get('token')
    print("main:",token)
    # 오늘의 Best
    if keyword == 'today':
        board_list = Board.objects.filter(
                                          datetime__year=date.year,
                                          datetime__month=date.month,
                                          datetime__day  = date.day
                                         ).exclude(category='16').order_by('-like_like','-id')
        active_index=1
    #주간 Best    
    elif keyword == 'weekday':
        week = date.weekday() + 7
        start_date = date - timedelta(days=week)
        date       = date - timedelta(days=date.weekday())
        start      = '{}-{}-{}'\
                       .format(start_date.year,start_date.month,start_date.day)
        end        = '{}-{}-{}'\
                       .format(date.year,date.month,date.day)

        board_list = Board.objects.filter(datetime__range=(start,end)).exclude(category='16').order_by('-like_like','-id')
        active_index = 2
    #월간 Best    
    elif keyword == 'month':
        start_date = date - relativedelta(months=1)
        start = '{}-{}-{}'.format(start_date.year,start_date.month,1)
        end   = '{}-{}-{}'.format(date.year,date.month,1)
        board_list = Board.objects.filter(datetime__range=(start,end)).exclude(category='16').order_by('-like_like','-id')
        active_index = 3
    #전체    
    elif keyword == 'all':
        board_list = Board.objects.all().exclude(category='16').order_by('-like_like','-id')
    else:
        raise Http404    
    
    info_list = Information.objects.all().order_by('-datetime')
    context = {
        'board_list':board_list[0:5],
        'active_index':active_index,
        'info_list':info_list,
        'token':token
    }
    return render(request,'main.html',context)

@csrf_exempt
def logout(request):
    context = {}
    token = request.POST.get('token')
    
    obj   = get_object(Tokens,token=token)
    if obj:
        obj.delete()
    return HttpResponse(json.dumps(context), content_type="application/json")

    
#로그인
@csrf_exempt
def login(request):
    context = {'isLogin':False}
    user = auth.authenticate(request,
                             username=request.POST.get('email'),
                             password=request.POST.get('pw')
                            )
    if user:
        token = account_activation_token.make_token(user)
        Tokens.objects.create(
            user  = user.profile,
            token = token
        )
        context['name']   = user.profile.name
        context['token']  = token 
        context['isLogin']=True
    return HttpResponse(json.dumps(context), content_type="application/json")


#회원가입
def register(request):
    is_register = False
    if request.method == "POST":
        birth_date = request.POST.get('birth_year')+'-'+ \
                     request.POST.get('birth_mon') +'-'+ \
                     request.POST.get('birth_day')

        user = User.objects.create_user(
                    username = request.POST.get('email'),
                    password = request.POST.get('pw')
                )

        Profile.objects.create(
            user      = user,
            name      = request.POST.get('name'),
            phone     = request.POST.get('phone'),
            birth_date= birth_date,
            gender    = request.POST.get('gender'),
            category  = request.POST.get('category')
        )        
        is_register = True

    return render(request,'register.html',{'is_register':is_register})


#게시판 쓰기
@csrf_exempt
def write_board(request):
    context={'isWrite':'False'}
    if request.GET.get('category'):
        context['category'] = request.GET.get('category')
    
    if request.method == "POST":
        print(request.POST.get('token'))
        token = get_object(Tokens,token=request.POST.get('token'))
        if not token:
            return HttpResponse("로그인 해주세요.")
        Board.objects.create(
            user     = token.user,
            title    = request.POST.get('title'),
            category = request.POST.get('category'),
            content  = request.POST.get('content')
        )
        context['isWrite'] = 'True'
    return render(request,"writeBoard.html",context)

def board_list(request):
    board_list = Board.objects.filter(category=request.GET.get('c_id'))
    page       = int(request.GET.get('page')) if request.GET.get('page') else 1
    token      = request.GET.get('token')
    
    board_list,start,end = page_nation(board_list,page)
    board = board_list[0] if board_list else None
    context = {
        'board_list' : board_list,
        'board'      : board,
        'token'      : token,
        'page_range' : range(start,end+1),
        'next'       : end + 1,
        'prev'       : start-1,
        'page'       : page
    }
    return render(request,'boardList.html',context)

def category(request):
    token = get_object(Tokens,token=request.GET.get('token'))
    return render(request,'category.html',{'token':token})

@csrf_exempt
def read_board(request,id):
    
    board = get_object_or_404(Board, id=id)
    
    hits_count(get_client_ip(request),board)
    token = request.GET.get('token')
    #token = '5lm-dcd36fdd110d32bf6712'
    comment_list = Comment.objects.filter(board=board).order_by('-id')
    print('read:',token)
    context = {
        'comment_list':comment_list,
        'board':board,
        'noti_comment_id':request.GET.get('noti_comment_id'),
        # 'token':'5lg-0c30f2ee75fb1f18009e'
        'token':token
    }
    return render(request,"readBoard.html",context)
def hits_count(ip, board):
    
    hits = get_object(HitCount,ip=ip,board=board)
    if hits:
        if not hits.date == timezone.now().date():
            board.hits = board.hits + 1
            board.save()
            hits.date = timezone.now()
            hits.save()
    else:

        HitCount.objects.create(
            ip=ip,
            board = board
        )
        board.hits = board.hits + 1
        board.save()
def get_client_ip(request):
    x_forwarded_for = request.META.get('HTTP_X_FORWARDED_FOR')
    if x_forwarded_for:
        ip = x_forwarded_for.split(',')[0]
    else:
        ip = request.META.get('REMOTE_ADDR')
    return ip    
def check_login(token,email):
    user  = get_object(User,username=email)
    token = get_object(Tokens,user=user.profile, token=token)
    if not user and not token:
        return True
    return False 
@csrf_exempt
def my_board(request,page):
    context = {}
    token = get_object(Tokens, token=request.GET.get('token')) 
    
    # token = get_object(Tokens,token='5lj-6f56cc9857f38ae8f5df')
    if not token:
        return HttpResponse("로그인 해주세요")
    
    board_list  = Board.objects.filter(user=token.user).order_by('-id')
    board_list,start,end = page_nation(board_list,page)
    
    context['board_list'] = board_list
    context['page_range'] = range(start,end+1)
    context['next']       = end + 1
    context['prev']       = start - 1
    context['page']       = page
    context['token']      = token.token
    

    return render(request,"myBoard.html",context)
@csrf_exempt
def deleteBoard(request):
    print(request.POST.getlist('check_list[]'))
    token = get_object(Tokens,token=request.POST.get('token'))
    print(token)
    # token = get_object(Tokens,token='5le-5b76649f6289b52993a0')
    if not token:
        return HttpResponse(json.dumps({'msg':False}), content_type="application/json")

    for id in list(request.POST.getlist('check_list[]')):
        board = get_object(Board,id=id,user=token.user)
        for comment in Comment.objects.filter(board=board):
            Comment.objects.filter(parent_id=comment.id).delete()
        board.delete()    
    print(token.token)
    context = {
        'msg':True,
        'token':token.token,
    }
    return HttpResponse(json.dumps(context), content_type="application/json")

@csrf_exempt
def file_upload(request):
    print(request.FILES.get('file'))
    context = {}
    file = request.FILES.get('file')
    print(file)
    if file:
        fs = FileSystemStorage() 
        f = fs.save(file.name, file) 
        context['url'] = fs.url(f) 

    return HttpResponse(json.dumps(context), content_type="application/json")
#게시판 수정
@csrf_exempt
def modify_board(request,id):
    context = {}
    
    if request.method == "POST":
        token = get_object(Tokens, token=request.POST.get('token'))
        if not token:
            return HttpResponse("게시판 수정실패 했습니다.")
        
        board = get_object(Board,id=id, user=token.user)
        if board:
            board.title    = request.POST.get('title')
            board.category = request.POST.get('category')
            board.content  = request.POST.get('content')
            board.save()
            context['board'] = board
            context['token'] = token
        context['isWrite'] = True
        return render(request,"writeBoard.html",context)
    else:
        board = get_object(Board,id=id)
        context['board']=board
        return render(request,"writeBoard.html",context)

@csrf_exempt
def write_comment(request):
    context = {}
    if request.method == "POST":
        token    = get_object(Tokens,token=request.POST.get('token'))
        if not token:
            return HttpResponse("로그인 해주세요.")
        print(token.user)    
        board = get_object(Board,id=request.POST.get('board_id')) 
        print(board)   
        comment = Comment.objects.create(
                        user = token.user,
                        board = board,
                        content = request.POST.get('content'),
                        parent_id = request.POST.get('parent_id')
                  )  
        if board:          
            Notification.objects.create(
                user = board.user,
                comment = comment
            )
        else:
            Notification.objects.create(
                user = comment.user,
                comment = comment
            )              
        
        context['comment_list'] = Comment.objects.filter(board=board,parent_id__isnull=True).order_by('-id')
        context['token'] = token.token
    return render(request,"comment.html",context)
@csrf_exempt
def read_recomment(request):
    parent_id = request.POST.get('parent_id')
    
    comment_list = Comment.objects.filter(parent_id = parent_id).order_by('-id')
    print(comment_list)
    context = {
        'parent_id':parent_id,
        'comment_list':comment_list
    }
    return render(request,'recomment.html',context)

#좋아요 클릭시
@csrf_exempt
def like(request,keyword):
    context = {}
    token = get_object(Tokens,token=request.POST.get('token'))
    print(token)
    if not token:
        return HttpResponse("로그인 해주세요")
    if keyword == 'board':
        board = get_object(Board,id=request.POST.get('board_id'))
        like = get_object(Like,board=board,user=token.user)
        print(board)
        if not like:
            board.like_like = board.like_like + 1
            board.save()
            Like.objects.create(
                user  = token.user,
                board = board,
            )
        context['board_like_cnt'] = board.like_like   
    elif keyword == 'comment':
        comment = get_object(Comment,id=request.POST.get('comment_id'))
        like = get_object(Like,comment=comment,user=token.user)
        if not like:
            comment.like_cnt = comment.like_cnt + 1
            comment.save()
            Like.objects.create(
                user = token.user,
                comment = comment
            )
        context['comment_like_cnt'] = comment.like_cnt    
        context['comment_id'] = comment.id
    else:
        raise Http404

    return HttpResponse(json.dumps(context), content_type="application/json")

@csrf_exempt
def my_commment(request):
    token = get_object(Tokens,token=request.POST.get('token'))
    print("hihihi")
    # token = get_object(Tokens,token='5lj-6f56cc9857f38ae8f5df')
    if not token:
        return HttpResponse("로그인 해주세요.")
    comment_list = Comment.objects.filter(user=token.user).order_by('-id')
    print(comment_list)
    context = {
        'comment_list':comment_list,
        'token':token.token
    }    
    return render(request,'myComment.html',context)
#댓글 수정
@csrf_exempt
def modify_comment(request,commentId):
    
    token = get_object(Tokens,token=request.POST.get('token'))
    if not token:
        return HttpResponse("로그인 해주세요.")
    comment = get_object(Comment,id=commentId,user=token.user)
    comment.content = request.POST.get('content')
    comment.save()
    context = {
        'id':comment.id,
        'content':comment.content,
    }
    return HttpResponse(json.dumps(context), content_type="application/json")

@csrf_exempt
def delete_comment(request):
    delete_list = []
    token = get_object(Tokens,token=request.POST.get('token'))
    if not token:
        return HttpResponse("로그인 해주세요.")
    comment = get_object(Comment,id=request.POST.get('comment_id'),user=token.user)
    for c in Comment.objects.filter(parent_id=comment.id):
        delete_list.append(c.id)
        c.delete()
    delete_list.append(comment.id)    
    context = {
        'id':delete_list,
        'parent_id':comment.parent_id
        }    
    comment.delete()

    return HttpResponse(json.dumps(context), content_type="application/json")
@csrf_exempt
def notification(request):
    token = get_object(Tokens,token=request.POST.get('token'))
    # token = get_object(Tokens,token='5lq-b760917a7703ad1c7082')
    print(token)
    noti_list = None
    if token:
        noti_list = Notification.objects.filter(user=token.user,user__isMessageAlarm=True)|\
                    Notification.objects.filter(user=token.user,user__isCommentAlarm=True)|\
                    Notification.objects.filter(user=token.user,user__isRecommentAlarm=True)

        noti_list = noti_list.order_by('-id')
        token = token.token

    context = {
        'token'       : token,
        'noti_list'   : noti_list,
    }    
    return render(request,'notification.html',context)

@csrf_exempt
def deleteNotification(request):
    token = get_object(Tokens,token=request.POST.get('token'))
    if not token:
        return HttpResponse("로그인 해주세요.")
    check_list = request.POST.getlist('check_list[]')
    print("checklist",check_list)
    if check_list:
        print(check_list)
        qs_noti    = Notification.objects.filter(id__in=check_list,user=token.user)
        qs_noti.delete()
    return  HttpResponse(json.dumps({'msg':True}), content_type="application/json")
@csrf_exempt
def write_message(request):
    context = {'isExist':True,'isWrite':False}
    if request.GET.get('username'):
       #게시판 작성한 사용자 아이디가 있으면
       context['username'] = request.GET.get('username')

    if request.method == "POST":
        token = get_object(Tokens,token=request.POST.get('token'))
        if not token:
            return HttpResponse("로그인 해주세요.")
        receive_user = get_object(User,username=request.POST.get('receive_user_id'))    
        if receive_user:
            
            msg_room = get_object(MessageRoom,user1=token.user,user2=receive_user.profile)
            if not msg_room:
                msg_room = get_object(MessageRoom,user1=receive_user.profile,user2=token.user)
            # room 없으면 room 생성
            if not msg_room:
                msg_room = MessageRoom.objects.create(
                                user1    = token.user,
                                user2    = receive_user.profile,            
                            )
                                
            message = Message.objects.create(
                        send_user=token.user,
                        receive_user = receive_user.profile,
                        content = request.POST.get('content'),
                        room    = msg_room
                    )

            Notification.objects.create(
                user = receive_user.profile,
                message = message
            )             
            context['isWrite']=True
            context['room_id'] = msg_room.id
            context['username'] = get_msg_receive_name(message,token.token)       
        else:
            '''
                쪽지 받는 사람 아이디가 존재하지 않으면 해당 블록 실행
            '''
            context['username'] = request.POST.get('receive_user_id')
            context['content']  = request.POST.get('content')
            context['isExist']  = False    
    return render(request,"writeMeassage.html",context)

def read_meassage(request):
    context = {}
    token = get_object(Tokens,token=request.GET.get('token'))
    qs_msg = None
    if not token:
        return HttpResponse("로그인 해주세요.")
    
    qs_msg = []
    
    qs_msg_room = MessageRoom.objects.filter(user1=token.user) | \
                  MessageRoom.objects.filter(user2=token.user)

    for room in qs_msg_room:
        msg_obj = Message.objects.filter(room=room).last()
        if msg_obj.send_user == token.user and not msg_obj.send_isDelete:
            qs_msg.append(msg_obj)
        elif msg_obj.receive_user == token.user and not msg_obj.receive_isDelete: 
            qs_msg.append(msg_obj)
    qs_msg = sorted(qs_msg)
    context['qs_msg'] = qs_msg
    context['token']  = token.token
    print(qs_msg)
    return render(request,'readMeassage.html',context)

def message_channel(request):
    token = get_object(Tokens,token=request.GET.get('token'))
    if not token:
        return HttpResponse("로그인 해주세요.")
    msg_room = get_object(MessageRoom,id=request.GET.get('room_id'))
    qs_msg   = []
    for msg_obj in Message.objects.filter(room=msg_room):
        # print(msg_obj.send_user == token.user)
        if msg_obj.send_user == token.user and not msg_obj.send_isDelete:
            qs_msg.append(msg_obj)
        elif msg_obj.receive_user == token.user and not msg_obj.receive_isDelete: 
            qs_msg.append(msg_obj)        
    
    qs_msg = sorted(qs_msg)
    context = {
        "token"  : token.token,
        "qs_msg" : qs_msg,
        'user'   : token.user
     }
    return render(request,'messageChannel.html',context)

#방삭제
def delete_room(request):
    token = get_object(Tokens,token=request.GET.get('token'))
    if not token:
        return HttpResponse("로그인 해주세요.")
    room = get_object(MessageRoom,id=request.GET.get('room_id'))
    for msg in Message.objects.filter(room=room):
        if msg.send_user == token.user:
            msg.send_isDelete = True
            msg.save()
        else:
            msg.receive_isDelete = True
            msg.save()
    return  HttpResponse(json.dumps({'msg':True}), content_type="application/json")

#검색
def search(request):
    keyword = request.GET.get('keyword')
    board_list = None
    if keyword:
        board_list = Board.objects.filter(Q(title__icontains=keyword) | Q(content__icontains=keyword))
    print(board_list)    
    context = {
        'board_list' : board_list,
        'keyword'    : keyword
    }
    return render(request,'search.html',context)

@csrf_exempt
def modify_user(request):
    token = get_object(Tokens,token=request.GET.get('token'))
    # token = get_object(Tokens,token='5lr-52e8fc204f57b690a3a1')
    if not token:
        return HttpResponse("로그인 해주세요.")

    print(token)
    
    context = {'isFinish':False,'user':token.user,'isChangePw':False,'token':token.token}
    if request.method == "POST":
        passwrd = request.POST.get('pw')
        if passwrd != "":
            token.user.user.set_password(passwrd)
            token.user.user.save()
            context['isChangePw'] = True

        birth_date = request.POST.get('birth_year')+'-'+ \
                     request.POST.get('birth_mon') +'-'+ \
                     request.POST.get('birth_day')
        token.user.name  = request.POST.get('name')   
        token.user.phone = request.POST.get('phone')   
        token.user.birth_date = birth_date
        token.user.gender  = request.POST.get('gender')

    return render(request,'modifyUser.html',context)

#이메일 중복 체크
def email_check(request):
    email     = request.GET.get('email')
    user_objs = User.objects.filter(username=email)
    context   = {}
    if user_objs:
        context['msg'] = '중복된 이메일입니다.'
    else:
        email_subject = '[창문] 회원가입 인증번호 입니다.'
        auth_number   = random.randint(1000,9999)
        message = '회원가입 인증번호 :{}'.format(auth_number)

        email_auth_obj = get_object(EmailAuthNumber,email=email)
        if email_auth_obj:
            email_auth_obj.auth_number = auth_number
            email_auth_obj.save()
        else:
            EmailAuthNumber.objects.create(
                email = email,
                auth_number = auth_number
            )    
        email = EmailMessage(email_subject, message, to=[email])
        email.send()
        context['msg'] = '인증번호가 발송되었습니다.'

        
    return HttpResponse(json.dumps(context), content_type="application/json")

@csrf_exempt
def check_auth_number(request):
    context = {}
    if request.method == "POST":
        auth_number    = request.POST.get('auth_number')
        email          = request.POST.get('email')
        email_auth_obj = get_object(EmailAuthNumber,email=email,auth_number=auth_number)
        if email_auth_obj:
            context['msg'] = '인증 되었습니다.'
            context['is_used'] = True
        else:
            context['msg'] = '인증번호가 잘못되었습니다.'
            context['is_used'] = False
    else:
        raise Http404
    
    return HttpResponse(json.dumps(context), content_type="application/json")

@csrf_exempt   
def quetion(request):
    board_list = Board.objects.filter(category='16').order_by('-id')
    page       = int(request.GET.get('page')) if request.GET.get('page') else 1
    token      = request.GET.get('token')
    
    board_list,start,end = page_nation(board_list,page)
    print(board_list)
    context = {
        'board_list' : board_list,
        'token'      : token,
        'page_range' : range(start,end+1),
        'next'       : end + 1,
        'prev'       : start-1,
        'page'       : page
    }
    return render(request,'quetion.html',context)

def program_info(request):
    return render(request,'programInfo.html')

def form(request,keyword):
    if keyword == "useService":
        return render(request,"useService.html")
    else:
        return render(request,"privacy.html")

def alarm(request):
    token = get_object(Tokens,token=request.GET.get('token'))
    if not token:
        return HttpResponse("로그인 해주세요.")
    context = {
        'token':token
    }
    return render(request,'alarm.html',context)    
@csrf_exempt   
def setting_alarm(request,keyword):
    token = get_object(Tokens,token=request.POST.get('token'))
    if not token:
        return HttpResponse("로그인 해주세요.")
    sw = request.POST.get('sw') 
    sw = True if sw == 'on' else False
    
    if keyword == 'message':
        
        token.user.isMessageAlarm = sw
        token.user.save()
    elif keyword == 'comment':
        token.user.isCommentAlarm = sw
        token.user.save()
    elif keyword == 'recomment':
        token.user.isRecommentAlarm = sw
        token.user.save()
    else:
        raise Http404

    return HttpResponse(json.dumps({'token':token.token}), content_type="application/json")

@csrf_exempt
def find_pw(request):
    context = {}
    if request.method == "POST":
        email = request.POST.get('email')
        phone = request.POST.get('phone')
        user = User.objects.filter(username=email,profile__phone=phone)
        if user:
            user = user[0]
            pw = get_temp_pw()
            user.set_password(pw)

            email = EmailMessage("임시비밀번호 입니다", "임시비밀번호 : {}".format(pw), to=[email])
            email.send()
            context['msg'] = '임시비밀번호가 발송 되었습니다.'

        else:
            context['msg'] = '이메일 또는 전환번호가 일치하지 않습니다.'
            context['email'] = email
            context['phone'] = phone
    return render(request,'findPw.html',context)

def get_temp_pw():
    String_result_passworld = ''

    for i in range(8):
        index = random.randrange(4)
        arr = [[33,39],[48,58],[65,91],[97,122]]
        String_result_passworld += chr(random.randrange(arr[index][0],arr[index][1]))
    return String_result_passworld    

