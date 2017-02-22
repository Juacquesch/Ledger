package ledger.io.input;

import au.com.bytecode.opencsv.CSVReader;
import ledger.database.entity.*;
import ledger.exception.ConverterException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Takes a CSV file generated by Chase Bank and converts into Ledger Object Structure
 */
public class ChaseCSVConverter extends AbstractCSVConverter {

    public ChaseCSVConverter(File file, Account account) {
        super(file, account);
    }

    @Override
    protected List<Transaction> readFile(CSVReader reader) throws ConverterException {
        List<Transaction> transactions = new LinkedList();

        try {
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                // String details = nextLine[0];
                String dateString = nextLine[1];
                String description = nextLine[2];
                String amountString = nextLine[3];
                String typeString = nextLine[4];
                String balanceString = nextLine[5];
                // String checkNumberString = nextLine[6];

                //Date date, Type type, int amount, Account account, Payee payee, boolean pending, List<Tag> tagList, Note note

                Date date = AbstractCSVConverter.df.parse(dateString);
                boolean pending = isPending(balanceString);
                int amount = (int) Math.round(Double.parseDouble(amountString) * 100);
                //int balance = 0;
                //if (!pending)
                  //  balance = (int) Math.round(Double.parseDouble(balanceString) * 100);

                Type type = TypeConversion.convert(typeString);

                // TODO: Should Payee be instantiated each time?
                description = description.substring(0, description.length() - 5).trim();
                Payee payee = new Payee(description, description);
                List<Tag> tags = null;
                Note note = null;

                Transaction transaction = new Transaction(date, type, amount, getAccount(), payee, pending, tags, note);

                transactions.add(transaction);
            }

        } catch (IOException e) {
            throw new ConverterException("Unable to read file.", e);
        } catch (ParseException e) {
            throw new ConverterException("File is not in the valid CSV format.", e);
        }
        return transactions;
    }

    private boolean isPending(String amount) {
        return amount == null || amount.trim().isEmpty();
    }
}
