-- Clients Table
CREATE TABLE IF NOT EXISTS clients (
                                       client_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       client_name TEXT NOT NULL,
                                       contact_info TEXT
);

-- Events Table
DROP TABLE IF EXISTS events;

CREATE TABLE events (
                        event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        event_name VARCHAR(255) NOT NULL,
                        event_artist VARCHAR(255) NOT NULL,
                        event_date TEXT NOT NULL,
                        event_time TEXT NOT NULL,
                        event_duration INTEGER NOT NULL,
                        required_capacity INTEGER NOT NULL,
                        event_type VARCHAR(255) NOT NULL,
                        event_category VARCHAR(50) NOT NULL,
                        client_id INTEGER NOT NULL,
                        FOREIGN KEY (client_id) REFERENCES clients(client_id)
);
DROP TABLE IF EXISTS venues;
-- Venues Table
CREATE TABLE IF NOT EXISTS venues (
                                      venue_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                      venue_name VARCHAR(255) NOT NULL,
                                        venue_category VARCHAR(50) NOT NULL,
                                        venue_capacity INTEGER NOT NULL,
                                        hire_price DECIMAL(10, 2) NOT NULL
    );

-- Bookings Table
CREATE TABLE IF NOT EXISTS bookings (
                                        booking_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        booking_date DATE NOT NULL,
                                        booking_hire_price REAL NOT NULL,
                                        booking_commission REAL NOT NULL,
                                        booking_status TEXT NOT NULL,
                                        event_id INTEGER NOT NULL,
                                        venue_id INTEGER NOT NULL,
                                        client_id INTEGER NOT NULL,
                                        booked_by INTEGER NOT NULL,
                                        FOREIGN KEY (event_id) REFERENCES events(event_id),
                                        FOREIGN KEY (venue_id) REFERENCES venues(venue_id),
                                        FOREIGN KEY (client_id) REFERENCES clients(client_id),
                                        FOREIGN KEY (booked_by) REFERENCES users(user_id)
    );

-- Users Table
CREATE TABLE IF NOT EXISTS users (
                                     user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     user_first_name VARCHAR(255) NOT NULL,
                                     user_last_name VARCHAR(255) NOT NULL,
                                     user_name VARCHAR(255) UNIQUE NOT NULL,
                                     user_password VARCHAR(255) NOT NULL,
                                     user_role VARCHAR(50) NOT NULL CHECK (user_role IN ('staff', 'manager'))
);

-- Suitabilities Table
CREATE TABLE IF NOT EXISTS suitabilities (
                                             suitability_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                             suitable_description TEXT NOT NULL
);

-- Suitabilities_Venues (Mapping Table)
CREATE TABLE IF NOT EXISTS suitabilities_venues (
                                                    suitability_id INTEGER NOT NULL,
                                                    venue_id INTEGER NOT NULL,
                                                    PRIMARY KEY (suitability_id, venue_id),
                                                    FOREIGN KEY (suitability_id) REFERENCES suitabilities(suitability_id),
                                                    FOREIGN KEY (venue_id) REFERENCES venues(venue_id)
    );
