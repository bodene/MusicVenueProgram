package service;

import model.Booking;
import model.Client;
import model.Event;
import model.User;
import model.Venue;

import java.io.*;
import java.util.List;


/**
 * Provides backup and restore functionality for transaction and master data.
 * <p>
 * The {@code BackupHandler} class handles the serialization and deserialization of transaction data
 * (i.e., bookings, events, and venues) as well as master data (i.e., users and clients) to and from
 * backup files. It uses Java object streams to write and read lists of objects.
 * </p>
 * <p>
 * This class is designed as a utility class with only static methods and cannot be instantiated.
 * </p>
 *
 * @author	Bodene Downie
 * @version 1.0
 */
public class BackupHandler {

	/* The file path for backing up transaction data */
	private static final String TRANSACTION_FILE_PATH = "transactiondata.lmvm";

	/* The file path for backing up master data */
	private static final String MASTER_BACKUP_FILE_PATH = "masterBackupData.lmvm";

	/**
	 * Private constructor to prevent instantiation.
	 */
	private BackupHandler(){}


	/**
	 * Backs up transaction data (bookings, events, and venues) to a file.
	 * <p>
	 * The method serializes the provided lists of {@code Booking}, {@code Event}, and {@code Venue} objects
	 * to the file specified by {@code TRANSACTION_FILE_PATH}. Venues are backed up along with their associated
	 * {@code VenueType} objects.
	 * </p>
	 *
	 * @param bookings the list of {@code Booking} objects to back up
	 * @param events   the list of {@code Event} objects to back up
	 * @param venues   the list of {@code Venue} objects to back up
	 */
	public static void backupTransactionData(List<Booking> bookings, List<Event> events, List<Venue> venues) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TRANSACTION_FILE_PATH))) {
			oos.writeObject(bookings);
			oos.writeObject(events);
			oos.writeObject(venues);  // Venues with VenueType objects
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restores the list of {@code Booking} objects from the transaction backup file.
	 *
	 * @return a {@code List<Booking>} restored from the backup file
	 * @throws IOException if an I/O error occurs while reading the backup file
	 * @throws ClassNotFoundException if the class of a serialized object cannot be found
	 */
	public static List<Booking> restoreBookings() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTION_FILE_PATH))) {
			return (List<Booking>) ois.readObject();
		}
	}


	/**
	 * Restores the list of {@code Event} objects from the transaction backup file.
	 * <p>
	 * This method reads the backup file and skips over the bookings list, then returns the list of events.
	 * </p>
	 *
	 * @return a {@code List<Event>} restored from the backup file
	 * @throws IOException if an I/O error occurs while reading the backup file
	 * @throws ClassNotFoundException if the class of a serialized object cannot be found
	 */
	public static List<Event> restoreEvents() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTION_FILE_PATH))) {
			ois.readObject();  // Skip bookings
			return (List<Event>) ois.readObject();
		}
	}

	/**
	 * Restores the list of {@code Venue} objects from the transaction backup file.
	 * <p>
	 * This method reads the backup file, skips the bookings and events lists, and then returns the list of venues.
	 * </p>
	 *
	 * @return a {@code List<Venue>} restored from the backup file
	 * @throws IOException if an I/O error occurs while reading the backup file
	 * @throws ClassNotFoundException if the class of a serialized object cannot be found
	 */
	public static List<Venue> restoreVenues() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TRANSACTION_FILE_PATH))) {
			ois.readObject();  // Skip bookings
			ois.readObject();  // Skip events
			return (List<Venue>) ois.readObject();
		}
	}

	/**
	 * Backs up master data (users and clients) to a file.
	 * <p>
	 * This method serializes the provided lists of {@code User} and {@code Client} objects to the file specified by
	 * {@code MASTER_BACKUP_FILE_PATH}.
	 * </p>
	 *
	 * @param users   the list of {@code User} objects to back up
	 * @param clients the list of {@code Client} objects to back up
	 */
	public static void backupMasterData(List<User> users, List<Client> clients) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MASTER_BACKUP_FILE_PATH))) {
			oos.writeObject(users);
			oos.writeObject(clients);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restores the list of {@code User} objects from the master backup file.
	 *
	 * @return a {@code List<User>} restored from the backup file
	 * @throws IOException if an I/O error occurs while reading the backup file
	 * @throws ClassNotFoundException if the class of a serialized object cannot be found
	 */
	public static List<User> restoreUsers() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MASTER_BACKUP_FILE_PATH))) {
			return (List<User>) ois.readObject();
		}
	}

	/**
	 * Restores the list of {@code Client} objects from the master backup file.
	 * <p>
	 * This method reads the backup file, skips the list of users, and returns the list of clients.
	 * </p>
	 *
	 * @return a {@code List<Client>} restored from the backup file
	 * @throws IOException if an I/O error occurs while reading the backup file
	 * @throws ClassNotFoundException if the class of a serialized object cannot be found
	 */
	public static List<Client> restoreClients() throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MASTER_BACKUP_FILE_PATH))) {
			ois.readObject();  // Skip users
			return (List<Client>) ois.readObject();
		}
	}
}