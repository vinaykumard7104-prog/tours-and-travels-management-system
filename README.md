# Tours & Travels Management System  (v2)

Java Swing + MySQL desktop app - rebuilt UI, repaired project, same database.

## Run it in NetBeans 22
1. **File > Open Project...** and pick this folder.
2. Make sure MySQL is running (Windows: *Services > MySQL80 > Start*).
3. Check `db.properties` (project root) - host / user / **password** must match what you use in MySQL Workbench.
4. Right-click the project > **Clean and Build**, then **Run** (F6).

The app creates the database and every table by itself on first start (and upgrades an
old database in place). `database/travelmanagementsystem.sql` is optional - it just lets you
view / edit the same schema in Workbench.

If the connection fails, the splash screen explains why and has a **Database settings**
button (saved to `~/.tours-travels/db.properties`, which overrides `db.properties`).

Requirements: JDK 17 - 22 (project is set to Java 17 source level), MySQL 5.7 / 8.x.
The JDBC driver is `lib/mysql-connector-java-8.0.28.jar` and is already on the project classpath.

## What was wrong in the old project (and is now fixed)
| Problem | Fix |
|---|---|
| `javac.classpath` was empty - the MySQL driver jar sat inside `src/`, so it was never loaded | Jar moved to `lib/` and added to the project libraries |
| No SQL schema anywhere | Schema + sample data created automatically; script provided |
| `UpdateCustomer` had no `WHERE` - it overwrote **every** customer | Profile save is limited to the signed-in user |
| SQL built by string concatenation (SQL injection) | `PreparedStatement` everywhere |
| Passwords stored as plain text | PBKDF2 hashes; old plain-text accounts still log in and are upgraded automatically |
| Connections never closed | try-with-resources, DB work off the UI thread |
| `bookeddetails.jpg` vs `bookedDetails.jpg`, missing `dest3`/`dest10` images | Images re-organised under `src/images/` and loaded safely |
| 10+ MB photos decoded at once -> the `hs_err_pid*.log` out-of-memory crashes | Images resized (34 MB -> 4 MB), loaded lazily and cached |
| Silver package showed Rs 24,000 but charged Rs 25,000 | One price, stored in the database |
| 19 separate pop-up windows, stray `java.java`, `rough.class`, empty `AddPackage`, crash logs, stale `build/` | Single-window app, clean package structure, junk removed |

## What's new
Modern single-window UI with animated sidebar, light/dark mode (top-right toggle, remembered),
page transitions, count-up statistics, live-priced booking dialogs, image gallery with
auto-playing hero, searchable hotels, cancel-booking, payment history with progress ring,
profile management, trip notes and a bill splitter.

## Notes
* Payments are a **simulated checkout** (no real payment gateway) - amount, method and a reference are saved.
* Prices/hotel rates are read from the `travelpackage` and `hotel` tables - edit them in Workbench and refresh.
* Package prices kept as in the old data (Gold 12,000 / Silver 24,000 / Bronze 32,000) - change them in `travelpackage` if the order looks odd to you.
* Destination photo captions were written from what the pictures show - adjust in `DestinationsPage.java` if needed.

## Project layout
```
src/tours/and/travels/management/systems/
  Main.java                entry point
  db/      DbConfig, Database (connection + auto-schema)
  model/   Models          (records)
  dao/     Account / Customer / Catalog / Booking / Payment DAOs
  util/    Passwords, Money, Validate, Async, Images, Session, Fmt
  ui/      Theme, Ico (vector icons), Anim, Hover, Ui
  ui/widget/  reusable components (Card, PillButton, Field, Combo, ...)
  ui/screen/  SplashFrame, AuthFrame, MainFrame, Sidebar, TopBar
  ui/page/    Home, Packages, Hotels, Destinations, Bookings, Payments, Profile, Tools
src/images/    hero/ destinations/ hotels/ packages/
lib/           mysql-connector-java-8.0.28.jar
```
