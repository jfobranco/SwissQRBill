//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.ValidationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the validation of the creditor data
 */
@DisplayName("Creditor validation")
class CreditorValidationTest extends BillDataValidationBase {
    @Test
    void validCreditor() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertNotNull(validatedBill.getCreditor());
        assertEquals("Zuppinger AG", validatedBill.getCreditor().getName());
        assertEquals("Industriestrasse", validatedBill.getCreditor().getStreet());
        assertEquals("34a", validatedBill.getCreditor().getHouseNo());
        assertEquals("9548", validatedBill.getCreditor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getCreditor().getTown());
        assertEquals("CH", validatedBill.getCreditor().getCountryCode());
    }

    @Test
    void missingCreditor() {
        bill = SampleData.getExample1();
        bill.setCreditor(null);
        validate();
        assertMandatoryPersonMessages();
    }

    @Test
    void emptyCreditor() {
        bill = SampleData.getExample1();
        Address emptyAddress = new Address();
        bill.setCreditor(emptyAddress);
        validate();
        assertMandatoryPersonMessages();
    }

    @Test
    void emptyCreditorWithSpaces() {
        bill = SampleData.getExample1();
        Address emptyAddress = new Address();
        emptyAddress.setName("  ");
        bill.setCreditor(emptyAddress);
        validate();
        assertMandatoryPersonMessages();
    }

    @Test
    void missingCreditorName() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName("  ");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_NAME, "field_is_mandatory");
    }

    @Test
    void creditorWithoutStreet() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setStreet(null);
        bill.setCreditor(address);
        validate();
        assertNoMessages();
    }

    @Test
    void creditorWithoutHouseNo() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setHouseNo(null);
        bill.setCreditor(address);
        validate();
        assertNoMessages();
    }

    @Test
    void creditorWithMissingPostalCode() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setPostalCode("");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_POSTAL_CODE, "field_is_mandatory");
    }

    @Test
    void creditorWithMissingTown() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setTown(null);
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_TOWN, "field_is_mandatory");
    }

    @Test
    void creditorWithMissingCountryCode() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setCountryCode("  ");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_COUNTRY_CODE, "field_is_mandatory");
    }

    @Test
    void creditorWithInvalidCountryCode() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setCountryCode("Schweiz");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_COUNTRY_CODE, "valid_country_code");
    }

    @Test
    void creditorWithInvalidCounturyCode2() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setCountryCode("R!");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_COUNTRY_CODE, "valid_country_code");
    }

    private void assertMandatoryPersonMessages() {
        assertTrue(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertTrue(result.hasMessages());
        assertEquals(4, result.getValidationMessages().size());
        for (ValidationMessage msg : result.getValidationMessages()) {
            assertEquals(ValidationMessage.Type.ERROR, msg.getType());
            assertEquals("field_is_mandatory", msg.getMessageKey());
            assertTrue(msg.getField().startsWith(Bill.FIELDROOT_CREDITOR));
        }
    }
}