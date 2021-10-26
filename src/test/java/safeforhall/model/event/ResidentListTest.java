package safeforhall.model.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static safeforhall.testutil.Assert.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import safeforhall.logic.parser.exceptions.ParseException;
import safeforhall.model.person.Person;
import safeforhall.testutil.TypicalPersons;

public class ResidentListTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ResidentList(null));
    }

    @Test
    public void constructor_nameRoomConflict_throwsIllegalArgumentException() {
        assertThrows(ParseException.class, () ->
                ResidentList.isValidResidentList("Peter, a213")); // name and room
    }

    @Test
    public void isValidResidentList() throws ParseException {
        // null name
        assertThrows(NullPointerException.class, () -> ResidentList.isValidResidentList(null));

        // invalid name
        assertFalse(ResidentList.isValidResidentList("")); // empty string
        assertFalse(ResidentList.isValidResidentList(" ")); // spaces only
        assertFalse(ResidentList.isValidResidentList("a213 b423")); // no comma between information

        // valid name
        assertTrue(ResidentList.isValidResidentList(ResidentList.DEFAULT_LIST)); // default no residents string
        assertTrue(ResidentList.isValidResidentList("peter jack")); // alphabets only
        assertTrue(ResidentList.isValidResidentList("Capital Tan")); // with capital letters
        assertTrue(ResidentList.isValidResidentList("peter jack, Capital Tan")); // more than one name
        assertTrue(ResidentList.isValidResidentList("peter jack,Capital Tan")); // comma no space
        assertTrue(ResidentList.isValidResidentList("a213")); // rooms only
        assertTrue(ResidentList.isValidResidentList("A213")); // rooms capital
        assertTrue(ResidentList.isValidResidentList("A213, b423")); // more than one room
    }

    @Test
    public void hasUnvaccinatedResident() {
        ResidentList emptyResidentList = new ResidentList(ResidentList.EMPTY_STRING);
        assertFalse(emptyResidentList.hasUnvaccinatedResident());
    }

    @Test
    public void numOfUnvaccinatedResidents() {
        ResidentList residentList = new ResidentList(ResidentList.DEFAULT_LIST);

        assertEquals(0, residentList.numOfUnvaccinatedResidents());
    }

    @Test
    public void isValidResidentStorage() throws ParseException {
        // null name
        assertThrows(NullPointerException.class, () -> ResidentList.isValidResidentStorage(null));

        // invalid name
        assertFalse(ResidentList.isValidResidentStorage("")); // empty string
        assertFalse(ResidentList.isValidResidentStorage(" ")); // spaces only
        assertFalse(ResidentList.isValidResidentStorage("David Li; Room: C112; Phone: 91031282; "
                + "Email: lidavid@example.com; Vaccinated: T; "
                + "Faculty: SDE; Last Fet Date: 02-10-2021;")); // missing a field
        assertFalse(ResidentList.isValidResidentStorage("Alex Yeoh; Room: E417; Phone: 87438807; "
                + "Email: alexyeoh@example.com; Vaccinated: T; Faculty: SOC; Last Fet Date: 01-10-2021; "
                + "Last Collection Date: 10-10-2021 Bernice Yu; Room: A213; Phone: 99272758; "
                + "Email: berniceyu@example.com; Vaccinated: F; Faculty: FASS; Last Fet Date: 10-10-2021; "
                + "Last Collection Date: 11-10-2021peter jack,Capital Tan")); // no comma between information

        // valid name
        assertTrue(ResidentList.isValidResidentStorage("None")); // default no residents string
        assertTrue(ResidentList.isValidResidentStorage("David Li; Room: C112; Phone: 91031282; "
                + "Email: lidavid@example.com; Vaccinated: T; Faculty: SDE; Last Fet Date: 02-10-2021; "
                + "Last Collection Date: 01-10-2021")); // single entry
        assertTrue(ResidentList.isValidResidentStorage("Alex Yeoh; Room: E417; Phone: 87438807; "
                + "Email: alexyeoh@example.com; Vaccinated: T; Faculty: SOC; Last Fet Date: 01-10-2021; "
                + "Last Collection Date: 10-10-2021, Bernice Yu; Room: A213; Phone: 99272758; "
                + "Email: berniceyu@example.com; Vaccinated: F; Faculty: FASS; Last Fet Date: 10-10-2021; "
                + "Last Collection Date: 11-10-2021, David Li; Room: C112; Phone: 91031282; "
                + "Email: lidavid@example.com; Vaccinated: T; Faculty: SDE; Last Fet Date: 02-10-2021; "
                + "Last Collection Date: 01-10-2021")); // multiple entries
        assertTrue(ResidentList.isValidResidentStorage("david li; room: C112; phone: 91031282; "
                + "Email: lidavid@example.com; vaccinated: T; faculty: SDE; last fet date: 02-10-2021; "
                + "last collection date: 01-10-2021")); // in lower case
        assertTrue(ResidentList.isValidResidentStorage("Alex Yeoh; Room: E417; Phone: 87438807; "
                + "Email: alexyeoh@example.com; Vaccinated: T; Faculty: SOC; Last Fet Date: 01-10-2021; "
                + "Last Collection Date: 10-10-2021,Bernice Yu; Room: A213; Phone: 99272758; "
                + "Email: berniceyu@example.com; Vaccinated: F; Faculty: FASS; Last Fet Date: 10-10-2021; "
                + "Last Collection Date: 11-10-2021")); // comma no space
        assertTrue(ResidentList.isValidResidentStorage("David Li;Room:C112;Phone:91031282;"
                + "Email: lidavid@example.com;Vaccinated:T;Faculty:SDE;Last Fet Date: 02-10-2021;"
                + "Last Collection Date: 01-10-2021")); // colon and semicolon no space
    }

    @Test
    public void getCombinedStorageStringTest() {
        ResidentList residentList = new ResidentList(ResidentList.DEFAULT_LIST);
        ArrayList<Person> toAdd = new ArrayList<>();
        toAdd.add(TypicalPersons.ALICE);

        // empty current, one person in toAdd
        String combinedString = residentList.getCombinedStorageString(toAdd);
        assertEquals(combinedString, TypicalPersons.ALICE.toString());

        // empty current, multiple persons in toAdd
        toAdd.add(TypicalPersons.BOB);
        toAdd.add(TypicalPersons.CARL);
        combinedString = residentList.getCombinedStorageString(toAdd);
        String expectedString = TypicalPersons.ALICE.toString()
                + ", "
                + TypicalPersons.BOB.toString()
                + ", "
                + TypicalPersons.CARL.toString();
        assertEquals(combinedString, expectedString);

        // current not empty
        residentList = new ResidentList(TypicalPersons.ELLE.getName().toString(), TypicalPersons.ELLE.toString());
        combinedString = residentList.getCombinedStorageString(toAdd);
        expectedString = TypicalPersons.ELLE.toString()
                + ", "
                + TypicalPersons.ALICE.toString()
                + ", "
                + TypicalPersons.BOB.toString()
                + ", "
                + TypicalPersons.CARL.toString();
        assertEquals(combinedString, expectedString);
    }

    @Test
    public void getCombinedDisplayStringTest() {
        ResidentList residentList = new ResidentList(ResidentList.DEFAULT_LIST);
        ArrayList<Person> toAdd = new ArrayList<>();
        toAdd.add(TypicalPersons.ALICE);

        // empty current, one person in toAdd
        String combinedString = residentList.getCombinedDisplayString(toAdd);
        assertEquals(combinedString, TypicalPersons.ALICE.getName().toString());

        // empty current, multiple persons in toAdd
        toAdd.add(TypicalPersons.BOB);
        toAdd.add(TypicalPersons.CARL);
        combinedString = residentList.getCombinedDisplayString(toAdd);
        String expectedString = TypicalPersons.ALICE.getName().toString()
                + ", "
                + TypicalPersons.BOB.getName().toString()
                + ", "
                + TypicalPersons.CARL.getName().toString();
        assertEquals(combinedString, expectedString);

        // current not empty
        residentList = new ResidentList(TypicalPersons.ELLE.getName().toString(), TypicalPersons.ELLE.toString());
        combinedString = residentList.getCombinedDisplayString(toAdd);
        expectedString = TypicalPersons.ELLE.getName().toString()
                + ", "
                + TypicalPersons.ALICE.getName().toString()
                + ", "
                + TypicalPersons.BOB.getName().toString()
                + ", "
                + TypicalPersons.CARL.getName().toString();
        assertEquals(combinedString, expectedString);
    }
}

