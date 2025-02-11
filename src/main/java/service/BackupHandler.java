package service;

import model.Booking;
import model.Client;
import model.Event;
import model.User;
import model.Venue;

import java.io.*;
import java.util.List;

public class BackupHandler {
	private static final String TRANSACTION_FILE_PATH = "transactiondata.lmvm";
	private static final String MASTER_BACKUP_FILE_PATH = "masterBackupData.lmvm";

	private BackupHandler(){}

	public static void backupTransactionData(List<Booking> bookings, List<Event> events, List<Venue> venues) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TRANSACTION_FILE_PATH))) {
			oos.writeObject(bookings);
			oos.writeObject(events);
			oos.writeObject(venues);  // Venues with VenueType objects
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Booking> restoreBookings() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTION_FILE_PATH))) {
			return (List<Booking>) ois.readObject();
		}
	}

	public static List<Event> restoreEvents() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTION_FILE_PATH))) {
			ois.readObject();  // Skip bookings
			return (List<Event>) ois.readObject();
		}
	}

	public static List<Venue> restoreVenues() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTION_FILE_PATH))) {
			ois.readObject();  // Skip bookings
			ois.readObject();  // Skip events
			return (List<Venue>) ois.readObject();
		}
	}



	public static void backupMasterData(List<User> users, List<Client> clients) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MASTER_BACKUP_FILE_PATH))) {
			oos.writeObject(users);
			oos.writeObject(clients);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<User> restoreUsers() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MASTER_BACKUP_FILE_PATH))) {
			return (List<User>) ois.readObject();
		}
	}

	public static List<Client> restoreClients() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MASTER_BACKUP_FILE_PATH))) {
			ois.readObject();  // Skip users
			return (List<Client>) ois.readObject();
		}
	}

}
