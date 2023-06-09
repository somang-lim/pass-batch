## 스프링 배치를 이용한 PT 이용권 관리 서비스


### 요구사항
* 이용권
  * 사용자는 N개의 이용권을 가질 수 있다.
  * 이용권은 횟수가 모두 소진되거나 이용기간이 지나면 만료된다.
  * 이용권 만료 전 사용자에게 알림을 준다.
  * 업체에서 원하는 시간을 설정하여 일괄적으로 사용자에게 이용권을 지급할 수 있다.
* 수업
  * 예약된 수업 10분 전에 출석 안내 알림을 준다.
  * 수업 종료 시간 시점, 수업을 예약한 학생의 이용권 횟수를 일괄로 차감한다.
* 통계
  * 사용자의 수업 예약, 출석, 이용권 횟수 등의 데이터로 유의미한 통계 데이터를 만든다.

<br>

### 개발 내용
|                           BATCH                            |                         VIEW                       |                        API                         |
|:----------------------------------------------------------:|:--------------------------------------------------:|:--------------------------------------------------:|
| 이용권 만료<br>이용권 일괄 지급<br>수업 전 알림<br>수업 후 이용권 차감<br>통계 데이터 구축 | 사용자 이용권 조회 페이지<br>관리자 이용권 등록 페이지<br>관리자 통계 조회 페이지 | 사용자 이용권 조회 API<br>관리자 이용권 등록 API<br>관리자 통계 조회 API  |

<br>

### GIT 커밋 컨벤션

|아이콘|코드|설명|
|:---:|:---:|:---|
|🎨|art| 코드의 구조/형태 개선 | 
|⚡️|zap|성능 개선|
|🔥|fire|코드/파일 삭제|
|🐛|bug|**버그 수정**|
|🚑|ambulance|긴급 수정|
|✨|sparkles|**새 기능**|
|📝|memo|문서 추가/수정|
|💄|lipstick|UI/스타일 파일 추가/수정|
|🎉|tada|프로젝트 시작|
|✅|white_check_mark|**테스트 추가/수정**|
|♻️|recycle|코드 리팩토링|
|➕|heavy_plus_sign|의존성 추가|
|➖|heavy_minus_sign|의존성 제거|
|🚚|truck|리소스 이동, 이름 변경|
|💡|bulb|주석 추가/수정|
|🗃|card_file_box|**데이터베이스 관련 수정**|
|🔊|loud_sound|로그 추가/수정|
|🙈|see_no_evil|.gitignore 추가/수정|

→ 깃모지 관련 내용: https://gitmoji.dev/
