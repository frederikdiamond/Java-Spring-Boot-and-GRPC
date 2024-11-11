create table farms (
   id serial primary key,
   name varchar(255) not null,
   location varchar(255) not null
);

create table animals (
     id serial primary key,
     registration_number varchar(50) not null unique,
     weight decimal not null,
     arrival_time timestamp not null,
     species varchar(255) not null,
     farm_id integer not null,
     FOREIGN KEY (farm_id) REFERENCES farms(id)
);

create table stations (
      id serial primary key,
      name varchar(255) not null
);

create table parts (
   id serial primary key,
   weight decimal not null,
   type varchar(255) not null
);

create table trays (
   id serial primary key,
   max_capacity decimal not null,
   current_weight decimal not null,
   part_type varchar(255) not null
);

create table products (
      id serial primary key,
      name varchar(255) not null,
      weight decimal not null,
      recalled boolean not null
);

CREATE TABLE retail_stores (
           id serial primary key,
           name varchar(255) not null,
           location varchar(255) not null
);

CREATE TABLE inventory (
       store_id integer not null,
       product_id integer not null,
       quantity integer not null default 0,
       primary key (store_id, product_id),
       foreign key (store_id) references retail_stores(id),
       foreign key (product_id) references products(id)
);

CREATE TABLE product_parts (
           product_id integer not null,
           part_id integer not null,
           primary key (product_id, part_id),
           foreign key (product_id) references products(id),
           foreign key (part_id) references parts(id)
);

CREATE TABLE animal_parts (
          animal_id INTEGER NOT NULL,
          part_id INTEGER NOT NULL,
          PRIMARY KEY (animal_id, part_id),
          FOREIGN KEY (animal_id) REFERENCES animals(id),
          FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE tray_parts (
        tray_id INTEGER NOT NULL,
        part_id INTEGER NOT NULL,
        PRIMARY KEY (tray_id, part_id),
        FOREIGN KEY (tray_id) REFERENCES trays(id),
        FOREIGN KEY (part_id) REFERENCES parts(id)
);

CREATE TABLE station_trays (
           station_id INTEGER NOT NULL,
           tray_id INTEGER NOT NULL,
           PRIMARY KEY (station_id, tray_id),
           FOREIGN KEY (station_id) REFERENCES stations(id),
           FOREIGN KEY (tray_id) REFERENCES trays(id)
);

/* insert data */

INSERT INTO farms (name, location) VALUES
                   ('Green Valley Farm', 'North Field'),
                   ('Sunny Acres', 'East Meadow');

INSERT INTO animals (registration_number, weight, arrival_time, species, farm_id) VALUES
                                             ('AN001',1200.5, '2023-10-01 08:30:00', 'Cow', 1),
                                             ('AN002',800.0, '2023-10-05 09:15:00', 'Pig', 1),
                                             ('AN003',1500.0, '2023-10-10 07:45:00', 'Cow', 2);

INSERT INTO stations (name) VALUES
            ('Slaughterhouse'),
            ('Butchery'),
            ('Packaging');

insert into parts (weight, type) VALUES
                 (500.0, 'Beef - Rib'),
                 (300.0, 'Beef - Sirloin'),
                 (200.0, 'Pork - Shoulder'),
                 (100.0, 'Pork - Belly');

INSERT INTO trays (max_capacity, current_weight, part_type) VALUES
                                            (1000.0, 500.0, 'Beef'),
                                            (800.0, 200.0, 'Pork');

INSERT INTO products (name, weight, recalled) VALUES
                              ('Premium Beef Pack', 800.0, FALSE),
                              ('Pork Delight Pack', 200.0, FALSE),
                              ('Beef Pack', 150.0, TRUE);

INSERT INTO retail_stores (name, location) VALUES
                           ('Meat Lovers Store', 'Downtown'),
                           ('Farm Fresh Market', 'Uptown');

INSERT INTO inventory (store_id, product_id, quantity) VALUES
                                       (1, 1, 50),
                                       (2, 2, 30);

INSERT INTO product_parts (product_id, part_id) VALUES
                                (1, 1),
                                (1, 2),
                                (2, 3);

INSERT INTO animal_parts (animal_id, part_id) VALUES
                              (1, 1),
                              (1, 2),
                              (2, 3);
