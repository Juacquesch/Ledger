package ledger.database.storage;

import ledger.database.IDatabase;
import ledger.database.enity.*;
import ledger.exception.StorageException;

import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Database handler for SQLite storage mechanism.
 */
public class SQLiteDatabase implements IDatabase {

    private Connection database;

    public SQLiteDatabase(InputStream iStream) throws StorageException {
        // Initialize SQLite streams.

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find SQLite Driver", e);
        }

        try {
            database = DriverManager.getConnection("jdbc:sqlite:src/test/resources/test.db");
        } catch (SQLException e) {
            throw new StorageException("Unable to connect to JDBC Socket. ");
        }

        initializeDatabase();
    }

    public void initializeDatabase() throws StorageException {
        LinkedList<String> tableSQL = new LinkedList<String>();

        tableSQL.add(tableTag);
        tableSQL.add(tableType);
        tableSQL.add(tableAccount);
        tableSQL.add(tableAccountBalance);
        tableSQL.add(tablePayee);

        tableSQL.add(tableTransaction);
        tableSQL.add(tableNote);

        tableSQL.add(tableTagToTrans);
        tableSQL.add(tableTagToPayee);

        try {
            for(String statement: tableSQL) {
                Statement stmt = database.createStatement();
                stmt.execute(statement);
                stmt.close();
            }
        } catch (SQLException e) {
            throw new StorageException("Unable to Create Table", e);
        }
    }

    private final String tableTransaction = "CREATE TABLE TRANSACTION " +
            "(TRANS_ID INT PRIMARY KEY  NOT NULL, " +
            "TRANS_DATETIME REAL        NOT NULL, " +
            "TRANS_AMOUNT INT           NOT NULL," +
            "TRANS_PENDING BOOLEAN      NOT NULL, " +
            "TRANS_ACCOUNT_ID INT       NOT NULL, " +
            "TRANS_PAYEE_ID INT         NOT NULL, " +
            "TRANS_TYPE_ID INT          NOT NULL, " +
            "FOREIGN KEY(TRANS_ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID), " +
            "FOREIGN KEY(TRANS_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)," +
            "FOREIGN KEY(TRANS_TYPE_ID) REFERENCES TYPE(TYPE_ID)" +
            ")";
    private final String tableNote = "CREATE TABLE NOTE" +
            "(NOTE_TRANS_ID INT PRIMARY KEY  NOT NULL, " +
            "NOTE_TEXT TEXT             NOT NULL, " +
            "FOREIGN KEY(NOTE_TRANS_ID) REFERENCES TRANSACTION(TRANS_ID)" +
            ")";
    private final String tableTagToTrans= "CREATE TABLE IF NOT EXISTS TAG_TO_TRANS " +
            "(TTTS_TAG_ID INT           NOT NULL, " +
            "TTTS_TRANS_ID INT          NOT NULL, " +
            "FOREIGN KEY(TTTS_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTTS_TRANS_ID) REFERENCES TRANSACTION(TRANS_ID)" +
            ")";
    private final String tableTag = "CREATE TABLE IF NOT EXISTS TAG " +
            "(TAG_ID INT PRIMARY KEY    NOT NULL, " +
            "TAG_NAME TEXT              NOT NULL, " +
            "TAG_DESC TEXT              NOT NULL" +
            ")";
    private final String tableType= "CREATE TABLE IF NOT EXISTS TYPE " +
            "(TYPE_ID INT PRIMARY KEY   NOT NULL, " +
            "TYPE_NAME TEXT              NOT NULL, " +
            "TYPE_DESC TEXT              NOT NULL" +
            ")";
    private final String tableAccount= "CREATE TABLE IF NOT EXISTS ACCOUNT, " +
            "(ACCOUNT_ID INT PRIMARY KEY NOT NULL, " +
            "ACCOUNT_NAME TEXT           NOT NULL, " +
            "ACCOUNT_DESC TEXT           NOT NULL" +
            ")";
    private final String tableAccountBalance= "CREATE TABLE IF NOT EXISTS ACCOUNT_BALANCE " +
            "(ABAL_ACCOUNT_ID INT       NOT NULL, " +
            "ABAL_DATETIME REAL         NOT NULL, " +
            "ABAL_AMOUNT INT            NOT NULL, " +
            "FOREIGN KEY(ABAL_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(ABAL_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)" +
            ")";
    private final String tablePayee= "CREATE TABLE IF NOT EXISTS PAYEE " +
            "(PAYEE_ID INT PRIMARY KEY  NOT NULL, " +
            "PAYEE_NAME TEXT           NOT NULL, " +
            "PAYEE_DESC TEXT           NOT NULL" +
            ")";
    private final String tableTagToPayee= "CREATE TABLE IF NOT EXISTS TAG_TO_PAYEE " +
            "(TTPE_TAG_ID INT           NOT NULL, " +
            "TTPE_PAYEE_ID INT          NOT NULL, " +
            "FOREIGN KEY(TTPE_TAG_ID) REFERENCES TAG(TAG_ID), " +
            "FOREIGN KEY(TTPE_PAYEE_ID) REFERENCES PAYEE(PAYEE_ID)" +
            ")";



    public void shutdown() throws StorageException {
        try {
            database.close();
        } catch (SQLException e) {
            throw new StorageException("Exception while shutting down database.", e);
        }
    }

    public void insertTransaction(Transaction transaction) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO TRANSACTION (TRANS_DATETIME,TRANS_AMOUNT,TRANS_PENDING,TRANS_ACCOUNT_ID,TRANS_PAYEE_ID) VALUES (?, ?, ?, ?, ?)");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());
            stmt.setBoolean(3, transaction.isPending());

            Account existingAccount = getAccountForName(transaction.getAccount().getName());
            if (existingAccount != null) {
                stmt.setInt(4, existingAccount.getId());
            } else {
                insertAccount(transaction.getAccount());
                Account insertedAccount = getAccountForName(transaction.getAccount().getName());
                stmt.setInt(4, insertedAccount.getId());
            }

            Payee existingPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
            if (existingPayee != null) {
                stmt.setInt(5, existingPayee.getId());
            } else {
                insertPayee(transaction.getPayee());
                Payee insertedPayee = getPayeeForNameAndDescription(transaction.getPayee().getName(), transaction.getPayee().getDescription());
                stmt.setInt(5, insertedPayee.getId());
            }

            stmt.executeUpdate();

            ResultSet generatedIDs = stmt.getGeneratedKeys();
            if (generatedIDs.next()) {
                int insertedTransactionID = generatedIDs.getInt("TRANS_ID");

                for (Tag currentTag : transaction.getTagList()) {
                    Tag existingTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
                    if (existingTag != null) {
                        insertTagToTrans(existingTag.getId(), insertedTransactionID);
                    } else {
                        insertTag(currentTag);
                        Tag insertedTag = getTagForNameAndDescription(currentTag.getName(), currentTag.getDescription());
                        insertTagToTrans(insertedTag.getId(), insertedTransactionID);
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding transaction", e);
        }
    }

    // TODO: Fix pls
    public void deleteTransaction(Transaction transaction) throws StorageException{
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM TRANSACTION WHERE TRANSACTION_ID = ?");
            stmt.setInt(1, transaction.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting transaction", e);
        }
    }

    // TODO: Fix pls
    public void editTransaction(Transaction transaction) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE TRANSACTION SET TRANS_DATETIME=?,TRANS_AMOUNT=?,TRANS_PENDING=?,TRANS_ACCOUNT_ID=?,TRANS_PAYEE_ID=? WHERE TRANS_ID=?");
            stmt.setLong(1, transaction.getDate().getTime());
            stmt.setInt(2, transaction.getAmount());
            stmt.setBoolean(3, transaction.isPending());
            stmt.setInt(4, transaction.getAccount().getId());
            stmt.setInt(5, transaction.getPayee().getId());
            stmt.setInt(6, transaction.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing transaction", e);
        }
    }

    // TODO: Fix pls
    public List<Transaction> getAllTransactions() throws StorageException {
        try {
            Statement stmt = database.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Transaction;");

            ArrayList<Transaction> transactionList = new ArrayList<Transaction>();

            while ( rs.next() ) {
                Date date = new Date(rs.getLong("TRANS_DATETIME"));
                int transactionID = rs.getInt("TRANS_ID");
                int amount = rs.getInt("TRANS_AMOUNT");
                boolean pending = rs.getBoolean("TRANS_PENDING");
                int accountID = rs.getInt("TRANS_ACCOUNT_ID");
                int payeeID = rs.getInt("TRANS_PAYEE_ID");

                Account account = getAccountFor(accountID);
                Payee payee = getPayeeForID(payeeID);
                List<Tag> tags = getTagsForTransactionID();
                Note note = getNoteForID(noteID);

                Transaction currentTransaction = new Transaction(date, type, amount, account, payee, pending, transactionID, tags, note);
            }
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all transactions", e);
        }
    }

    public void insertAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO ACCOUNT (ACCOUNT_NAME,ACCOUNT_DESC) VALUES (?, ?)");
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getDescription());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Account", e);
        }
    }


    public void deleteAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM ACCOUNT WHERE ACCOUNT_ID = ?");
            stmt.setInt(1, account.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Account", e);
        }
    }


    public void editAccount(Account account) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE ACCOUNT SET ACCOUNT_NAME=?, ACCOUNT_DESC=? WHERE ACCOUNT_ID=?");
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getDescription());
            stmt.setInt(3, account.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Account", e);
        }
    }

    public void insertPayee(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt =
                    database.prepareStatement("INSERT INTO PAYEE (PAYEE_NAME, PAYEE_DESC) VALUES (?, ?, ?)");
            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Payee", e);
        }
    }


    public void deletePayee(Payee payee) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM PAYEE WHERE PAYEE_ID = ?");
            stmt.setInt(1, payee.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Payee", e);
        }
    }


    public void editPayee(Payee payee) throws StorageException {

        try {
            PreparedStatement stmt =
                    database.prepareStatement("UPDATE PAYEE SET PAYEE_NAME?, PAYEE_DESC? WHERE PAYEE_ID = ?");

            stmt.setString(1, payee.getName());
            stmt.setString(2, payee.getDescription());
            stmt.setInt(3, payee.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing Payee", e);
        }
    }


    public void insertType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("INSERT INTO TYPE (TYPE_NAME,TYPE_DESC) VALUES (?, ?)");
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while adding Type", e);
        }
    }


    public void deleteType(Type type) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("DELETE FROM TYPE WHERE TYPE_ID = ?");
            stmt.setInt(1, type.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while deleting Type", e);
        }
    }

    public void editType(Type type) {
        try {
            PreparedStatement stmt = database.prepareStatement("UPDATE TYPE SET TYPE_NAME=?, TYPE_DESC=? WHERE TRANS_ID=?");
            stmt.setString(1, type.getName());
            stmt.setString(2, type.getDescription());
            stmt.setInt(3, type.getId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while editing type")
        }
    }

    private Account getAccountForName(String name) throws StorageException {
        try {
            PreparedStatement stmt = database.prepareStatement("SELECT * FROM ACCOUNT WHERE ACCOUNT_NAME=?");
            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();
            int count = 0;

            String newName = "";
            String description = "";
            int id = -1;

            while (rs.next()) {
                newName = rs.getString("ACCOUNT_NAME");
                description = rs.getString("ACCOUNT_DESC");
                id = rs.getInt("ACCOUNT_ID");
            }

            if (count == 0) return null;

            return new Account(newName, description, id);

        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting Account by Name", e);
        }
    }
}
