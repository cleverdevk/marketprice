create table user(
  id varchar(30) not null,
  password varchar(255) not null,
  nickname varchar(10) not null
);

create table accounting(
  no int not null AUTO_INCREMENT,
  id VARCHAR(30) not null,
  lat DECIMAL(13,10),
  lng DECIMAL(13,10),
  title VARCHAR(30),
  content VARCHAR(100),
  start_time char(10),
  end_time char(10),
  member int(2),
  share BOOLEAN,

  CONSTRAINT accounting_PK PRIMARY KEY (no),
  CONSTRAINT accounting_FK FOREIGN KEY (id) REFERENCES user(id)

);

create table accounting_elements(
  accountingno int not null,
  no int not null,
  name varchar(30),
  cost DECIMAL(10,2),

  CONSTRAINT accounting_elements_PK PRIMARY KEY (accountingno, no),
  CONSTRAINT accounting_elements_FK FOREIGN KEY (accountingno) REFERENCES accounting(no)
);

create table review_goods(
  no int not null AUTO_INCREMENT,
  id VARCHAR(30) not null,
  lat DECIMAL(13,10),
  lng DECIMAL(13,10),
  imageurl VARCHAR(100),
  content VARCHAR(100),
  rate int(2),
  cost DECIMAL(10,2),
  name VARCHAR(30),
  good int DEFAULT 0,
  bad int DEFAULT 0,

  CONSTRAINT review_goods_PK PRIMARY KEY (no),
  CONSTRAINT review_goods_FK FOREIGN KEY (id) REFERENCES user(id)
);

create table review_food(
  no int not null AUTO_INCREMENT,
  id VARCHAR(30) not null,
  lat DECIMAL(13,10),
  lng DECIMAL(13,10),
  imageurl VARCHAR(100),
  content VARCHAR(100),
  rate int(2),
  cost DECIMAL(10,2),
  name VARCHAR(30),
  good int DEFAULT 0,
  bad int DEFAULT 0,

  CONSTRAINT review_food_PK PRIMARY KEY (no),
  CONSTRAINT review_food_FK FOREIGN KEY (id) REFERENCES user(id)
);

create table review_transport(
  no int not null AUTO_INCREMENT,
  id VARCHAR(30) not null,
  start_lat DECIMAL(13,10),
  start_lng DECIMAL(13,10),
  end_lat DECIMAL(13,10),
  end_lng DECIMAL(13,10),
  distance DECIMAL(6,2),
  cost DECIMAL(10,2),
  timeslot int(2),
  name VARCHAR(30),

  CONSTRAINT review_transport_PK PRIMARY KEY (no),
  CONSTRAINT review_transport_FK FOREIGN KEY (id) REFERENCES user(id)
);