package de.salt.sce.mapper.util;

import de.salt.sce.mapper.exception.FailedModuloCalculationException;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;

import static java.lang.Integer.parseInt;

/**
 * <p>Modulo 10 + 1 Procedure</p>
 * <p>Used for example on GLS Module</p>
 *
 * @author wrh
 * @since 3.0.2
 */
public class Modulo10Plus1 {

    public static int calculate(String code) throws NumberFormatException {
        EAN13CheckDigit checker = new EAN13CheckDigit();
        String result;
        try {
            result = checker.calculate(code);
        } catch (CheckDigitException | NumberFormatException e) {
            throw new FailedModuloCalculationException();
        }
        int intResult = parseInt(result);

        if (intResult == 0)
            return 9;
        else
            return intResult - 1;
    }
}
