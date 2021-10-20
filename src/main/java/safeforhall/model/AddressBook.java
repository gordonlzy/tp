package safeforhall.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import safeforhall.model.event.Event;
import safeforhall.model.event.EventName;
import safeforhall.model.event.UniqueEventList;
import safeforhall.model.event.exceptions.EventNotFoundException;
import safeforhall.model.person.Name;
import safeforhall.model.person.Person;
import safeforhall.model.person.Room;
import safeforhall.model.person.UniquePersonList;
import safeforhall.model.person.exceptions.PersonNotFoundException;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSamePerson comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private final UniqueEventList events;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        persons = new UniquePersonList();
        events = new UniqueEventList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Replaces the contents of the event list with {@code events}.
     * {@code events} must not contain duplicate events.
     */
    public void setEvents(List<Event> events) {
        this.events.setEvents(events);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setEvents(newData.getEventList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Returns true if an event with the same details as {@code event} exists in the address book.
     */
    public boolean hasEvent(Event event) {
        requireNonNull(event);
        return events.contains(event);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Adds an event to the address book.
     * The event must not already exist in the address book.
     */
    public void addEvent(Event e) {
        events.add(e);
    }

    /**
     * Finds the event list for an event from the given Event Name.
     */
    public Optional<Event> findEvent(EventName eventName) {
        for (Event event : events) {
            if (event.hasSameEventName(eventName)) {
                return Optional.of(event);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds the person list for an event from the given Room.
     */
    public Optional<Person> findPerson(Room room) {
        for (Person person : persons) {
            if (person.isStayingInRoom(room)) {
                return Optional.of(person);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds the person list for an event from the given Room.
     */
    public Optional<Person> findPerson(Name name) {
        for (Person person : persons) {
            if (person.hasTheName(name)) {
                return Optional.of(person);
            }
        }
        return Optional.empty();
    }

    /**
     * Replaces the given event {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The event identity of {@code editedPerson} must not be the same as another existing event in the address book.
     */
    public void setEvent(Event target, Event editedEvent) throws EventNotFoundException {
        requireNonNull(editedEvent);

        events.setEvent(target, editedEvent);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) throws PersonNotFoundException {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeEvent(Event key) {
        events.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return persons.asUnmodifiableObservableList().size() + " persons";
        // TODO: refine later
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Event> getEventList() {
        return events.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddressBook // instanceof handles nulls
                && persons.equals(((AddressBook) other).persons)
                && events.equals(((AddressBook) other).events));
    }

    @Override
    public int hashCode() {
        return persons.hashCode();
    }
}
