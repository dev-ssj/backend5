FROM nginx:latest

#젠킨스에 올릴 때, 우리가 만든 설정파일이 설정되지않음
#-> 여기서 nginx.conf복사를 해서 강제적으로 덮어씌운다.

#기존 설정 제거(선택적)
RUN rm /etc/nginx/nginx.conf

#커스텀 nginx.conf 복사
COPY ./nginx.conf /etc/nginx/nginx.conf