package safeforhall.model;

import static java.util.Objects.requireNonNull;
import static safeforhall.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import safeforhall.commons.core.GuiSettings;
import safeforhall.commons.core.LogsCenter;
import safeforhall.logic.commands.exceptions.CommandException;
import safeforhall.model.event.Event;
import safeforhall.model.event.EventName;
import safeforhall.model.event.ResidentList;
import safeforhall.model.person.Name;
import safeforhall.model.person.Person;
import safeforhall.model.person.Room;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Event> filteredEvents;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredEvents = new FilteredList<>(this.addressBook.getEventList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /**
     * Converts a {@code ResidentList} that can have a String of Room or Name to an Arraylist of
     * {@code Person}
     */
    @Override
    public ArrayList<Person> toPersonList(ResidentList residentList) throws CommandException {
        requireNonNull(residentList);
        ArrayList<String> residentInformation = residentList.residentInformation;
        ArrayList<Person> personList = new ArrayList<>();

        for (String information : residentInformation) {
            Optional<Person> personFound;
            if (Room.isValidRoom(information)) {
                Room room = new Room(information);
                personFound = addressBook.findPerson(room);
            } else if (Name.isValidName(information)) {
                Name name = new Name(information);
                personFound = addressBook.findPerson(name);
            } else {
                throw new IllegalArgumentException();
            }

            if (personFound.isEmpty()) {
                throw new CommandException(information + " person not found");
            } else {
                personList.add(personFound.get());
            }
        }
        return personList;
    }

    /**
     * Reads a string of name from {@code ResidentList} and return an Arraylist of {@code Person}
     */
    @Override
    public ArrayList<Person> getCurrentEventResidents(ResidentList residentList) throws CommandException {
        requireNonNull(residentList);
        String[] residentInformation = residentList.residents.split("\\s*,\\s*");

        ArrayList<Person> personList = new ArrayList<>();
        for (String information : residentInformation) {
            Optional<Person> personFound;
            if (Name.isValidName(information)) {
                Name name = new Name(information);
                personFound = addressBook.findPerson(name);
            } else {
                throw new IllegalArgumentException();
            }

            if (personFound.isEmpty()) {
                throw new CommandException(information.toString() + " event not found");
            } else {
                personList.add(personFound.get());
            }
        }
        return personList;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public boolean hasEvent(Event event) {
        requireNonNull(event);
        return addressBook.hasEvent(event);
    }

    @Override
    public Event getEvent(EventName eventName) throws CommandException {
        requireNonNull(eventName);
        Optional<Event> eventOptional = addressBook.findEvent(eventName);
        if (eventOptional.isEmpty()) {
            throw new CommandException(eventName + " not found");
        } else {
            return eventOptional.get();
        }
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void deleteEvent(Event target) {
        addressBook.removeEvent(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void addEvent(Event event) {
        addressBook.addEvent(event);
        updateFilteredEventList(PREDICATE_SHOW_ALL_EVENTS);
    }

    @Override
    public void setEvent(Event target, Event editedEvent) {
        requireAllNonNull(target, editedEvent);

        addressBook.setEvent(target, editedEvent);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public ObservableList<Event> getFilteredEventList() {
        return filteredEvents;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public void updateFilteredEventList(Predicate<Event> predicate) {
        requireNonNull(predicate);
        filteredEvents.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && userPrefs.equals(other.userPrefs)
                && filteredPersons.equals(other.filteredPersons)
                && filteredEvents.equals(other.filteredEvents);
    }

}
