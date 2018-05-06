//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.ValidationMessage;
import net.codecrete.qrbill.generator.ValidationResult;


/**
 * Unit tests for decoding the embedded QR code text
 */
@DisplayName("Decoding of embedded QR code text")
class DecodedTextTest {

    private static final String VALID_TEXT = "SPC\r\n" +
    "0100\r\n" +
    "1\r\n" +
    "CH4431999123000889012\r\n" +
    "Robert Schneider AG\r\n" +
    "Rue du Lac\r\n" +
    "1268/2/22\r\n" +
    "2501\r\n" +
    "Biel\r\n" +
    "CH\r\n" +
    "Robert Schneider Services Switzerland AG\r\n" +
    "Rue du Lac\r\n" +
    "1268/3/1\r\n" +
    "2501\r\n" +
    "Biel\r\n" +
    "CH\r\n" +
    "123949.75\r\n" +
    "CHF\r\n" +
    "2019-10-31\r\n" +
    "Pia-Maria Rutschmann-Schnyder\r\n" +
    "Grosse Marktgasse\r\n" +
    "28\r\n" +
    "9400\r\n" +
    "Rorschach\r\n" +
    "CH\r\n" +
    "QRR\r\n" +
    "210000000003139471430009017\r\n" +
    "Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010";

    @Test
    void decodeText1() {
        Bill bill = SampleData.getExample1();
        Bill bill2 = QRBill.decodeQrCodeText(QRBill.encodeQrCodeText(bill));
        bill = QRBill.validate(bill).getCleanedBill();
        assertEquals(bill, bill2);
    }

    @Test
    void decodeText2() {
        Bill bill = SampleData.getExample2();
        Bill bill2 = QRBill.decodeQrCodeText(QRBill.encodeQrCodeText(bill));
        bill2.getCreditor().setStreet(null); // replace empty string with null
        bill2.getCreditor().setHouseNo(null); // replace empty string with null
        bill2.setLanguage(bill.getLanguage()); // fix language (not contained in text)
        assertEquals(bill, bill2);
    }

    @Test
    void decodeText3() {
        Bill bill = SampleData.getExample3();
        Bill bill2 = QRBill.decodeQrCodeText(QRBill.encodeQrCodeText(bill));
        bill2.setLanguage(bill.getLanguage()); // fix language (not contained in text)
        bill2.setAdditionalInfo(null); // replace empty string with null
        assertEquals(bill, bill2);
    }

    @Test
    void decodeText4() {
        Bill bill = SampleData.getExample4();
        Bill bill2 = QRBill.decodeQrCodeText(QRBill.encodeQrCodeText(bill));
        bill2.getCreditor().setStreet(null); // replace empty string with null
        bill2.getCreditor().setHouseNo(null); // replace empty string with null
        bill2.setLanguage(bill.getLanguage()); // fix language (not contained in text)
        assertEquals(bill, bill2);
    }

    @Test
    void decodeInvalidFormat1() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> {
            QRBill.decodeQrCodeText("garbage");
        });
        assertSingleError(err.getValidationResult(), QRBill.KEY_VALID_DATA_STRUCTURE, Bill.FIELD_QR_TYPE);
    }

    @Test
    void decodeInvalidFormat2() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> {
            QRBill.decodeQrCodeText("SPC\r\n0100\r\n\r\n\r\n");
        });
        assertSingleError(err.getValidationResult(), QRBill.KEY_VALID_DATA_STRUCTURE, Bill.FIELD_QR_TYPE);
    }

    @Test
    void decodeInvalidFormat3() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> {
            QRBill.decodeQrCodeText("SPC1\r\n0100\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n");
        });
        assertSingleError(err.getValidationResult(), QRBill.KEY_VALID_DATA_STRUCTURE, Bill.FIELD_QR_TYPE);
    }

    @Test
    void decodeInvalidVersion() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> {
            QRBill.decodeQrCodeText("SPC\r\n0101\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n");
        });
        assertSingleError(err.getValidationResult(), QRBill.KEY_SUPPORTED_VERSION, Bill.FIELD_VERSION);
    }

    @Test
    void decodeInvalidCodingType() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> {
            QRBill.decodeQrCodeText("SPC\r\n0100\r\n0\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n");
        });
        assertSingleError(err.getValidationResult(), QRBill.KEY_SUPPORTED_CODING_TYPE, Bill.FIELD_CODING_TYPE);
    }

    @Test
    void decodeInvalidNumber() {
        String invalidText = VALID_TEXT.replace("123949.75", "1239d49.75");
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> {
            QRBill.decodeQrCodeText(invalidText);
        });
        assertSingleError(err.getValidationResult(), QRBill.KEY_VALID_NUMBER, Bill.FIELD_AMOUNT);
    }

    @Test
    void decodeInvalidDate1() {
        String invalidText = VALID_TEXT.replace("2019-10-31", "2019-10-32");
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> {
            QRBill.decodeQrCodeText(invalidText);
        });
        assertSingleError(err.getValidationResult(), QRBill.KEY_VALID_DATE, Bill.FIELD_DUE_DATE);
    }

    @Test
    void decodeInvalidDate2() {
        String invalidText = VALID_TEXT.replace("2019-10-31", "31.10.2019");
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> {
            QRBill.decodeQrCodeText(invalidText);
        });
        assertSingleError(err.getValidationResult(), QRBill.KEY_VALID_DATE, Bill.FIELD_DUE_DATE);
    }

    private void assertSingleError(ValidationResult result, String messageKey, String field) {
        assertNotNull(result);
        List<ValidationMessage> messages = result.getValidationMessages();
        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals(ValidationMessage.Type.ERROR, messages.get(0).getType());
        assertEquals(messageKey, messages.get(0).getMessageKey());
        assertEquals(field, messages.get(0).getField());
    }
}