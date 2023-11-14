
FROM ubuntu:latest

# Setting timezone to avoid hanging up with tzdata!
ENV TZ=Asia/Dubai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


RUN apt-get update

# Installing github for further use
RUN apt-get install git -y

# Installing NGINX 
RUN apt-get install nginx -y

# Installing required PHP modules
RUN apt-get install php -y
RUN apt-get install php-phar -y
RUN apt-get install php-xml -y


# Installing composer in order to install Lumen
RUN php -r "copy('http://getcomposer.org/installer', 'composer-setup.php');" && \
    php composer-setup.php --install-dir=/usr/bin --filename=composer && \
    php -r "unlink('composer-setup.php');"

# Move our app into the docker
COPY . /lumen
WORKDIR /lumen/app

CMD ["php", "-S","0.0.0.0:8000", "-t", "public"]