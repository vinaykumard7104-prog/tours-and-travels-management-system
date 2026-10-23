-- =====================================================================
--  Tours & Travels Management System  -  database script
--  Run in MySQL Workbench (File > Open SQL Script > Execute).
--
--  NOTE: the application creates all of this automatically on first
--  start, so running this script is OPTIONAL. It is provided so you can
--  see / edit the schema and sample data in Workbench.
--  It is safe to run more than once (nothing is dropped).
-- =====================================================================
CREATE DATABASE IF NOT EXISTS travelmanagementsystem DEFAULT CHARACTER SET utf8mb4;
USE travelmanagementsystem;

CREATE TABLE IF NOT EXISTS account (
  username VARCHAR(50) NOT NULL PRIMARY KEY,
  name     VARCHAR(100),
  password VARCHAR(255),          -- PBKDF2 hash (old plain-text values still work)
  security VARCHAR(150),          -- security question
  answer   VARCHAR(255)           -- hashed answer
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS customer (
  username VARCHAR(50), id VARCHAR(50), number VARCHAR(60), name VARCHAR(100),
  gender VARCHAR(20), country VARCHAR(60), address VARCHAR(255),
  phone VARCHAR(25), email VARCHAR(120)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bookpackage (
  booking_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50), `package` VARCHAR(60), persons VARCHAR(10),
  id VARCHAR(50), number VARCHAR(60), phone VARCHAR(25), price VARCHAR(30),
  booked_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bookhotels (
  booking_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50), name VARCHAR(100), persons VARCHAR(10), days VARCHAR(10),
  ac VARCHAR(10), food VARCHAR(10), id VARCHAR(50), number VARCHAR(60),
  phone VARCHAR(25), price VARCHAR(30),
  booked_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS hotel (
  name VARCHAR(100), costperperson VARCHAR(20),   -- per person per night
  foodincluded VARCHAR(20),                       -- meals surcharge (per person per night)
  acroom VARCHAR(20)                              -- AC surcharge (per person per night)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS travelpackage (
  name VARCHAR(60) NOT NULL PRIMARY KEY, tagline VARCHAR(120), duration VARCHAR(60),
  price INT NOT NULL, season VARCHAR(40), features TEXT, image VARCHAR(120)
) DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS payment (
  payment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL,
  amount INT NOT NULL, method VARCHAR(30), reference VARCHAR(40),
  paid_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARSET=utf8mb4;

-- ---- sample catalogue (only inserted when the tables are empty) ----
INSERT INTO travelpackage (name, tagline, duration, price, season, features, image)
SELECT * FROM (
  SELECT 'Gold Package' n, 'Beach & island escape' t, '6 Days and 7 Nights' d, 12000 p, 'SUMMER SPECIAL' s,
         'Airport Assistance|Half Day City Tour|Daily Buffet|Soft Drinks|Full Day 3 Island Cruise|English Speaking Guide' f, 'package1.jpg' i
  UNION ALL SELECT 'Silver Package', 'Mountains & night safari', '5 Days and 6 Nights', 24000, 'WINTER SPECIAL',
         'Toll Free|Entrance Free Tickets|Meet And Greet at Airport|Welcome Drinks on Arrival|Night Safari|Cruise with Dinner', 'package2.jpg'
  UNION ALL SELECT 'Bronze Package', 'Backwaters & adventure', '6 Days and 5 Nights', 32000, 'WINTER SPECIAL',
         'Return Airfare|Free Clubbing, Horse Riding & Games|River Rafting|Hard Drinks Free|Daily Buffet|BBQ Dinner', 'package3.jpg'
) x WHERE NOT EXISTS (SELECT 1 FROM travelpackage);

INSERT INTO hotel (name, costperperson, foodincluded, acroom)
SELECT * FROM (
  SELECT 'JW Marriott Hotel' n, '4500' c, '800' f, '700' a
  UNION ALL SELECT 'Mandarin Oriental Hotel', '5200', '900', '800'
  UNION ALL SELECT 'Four Seasons Hotel', '5800', '1000', '900'
  UNION ALL SELECT 'Radisson Blue Hotel', '3200', '600', '500'
  UNION ALL SELECT 'Classico Hotel', '1800', '400', '350'
  UNION ALL SELECT 'The Bay Club Hotel', '2600', '500', '450'
  UNION ALL SELECT 'Breeze Blow Hotel', '1500', '350', '300'
  UNION ALL SELECT 'The Taj Hotel', '6500', '1100', '1000'
  UNION ALL SELECT 'Happy Morning Motel', '900', '250', '200'
  UNION ALL SELECT 'River View Hotel', '2100', '450', '400'
) x WHERE NOT EXISTS (SELECT 1 FROM hotel);
