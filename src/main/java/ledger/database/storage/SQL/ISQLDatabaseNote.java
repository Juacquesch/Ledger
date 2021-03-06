package ledger.database.storage.SQL;

import ledger.database.entity.Note;
import ledger.database.storage.SQL.SQLite.ISQLiteDatabase;
import ledger.database.storage.table.NoteTable;
import ledger.exception.StorageException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public interface ISQLDatabaseNote extends ISQLiteDatabase {

    @Override
    default void insertNote(Note note) throws StorageException {
        try {
            PreparedStatement stmt =
                    getDatabase().prepareStatement("INSERT INTO " + NoteTable.TABLE_NAME +
                            " (" + NoteTable.NOTE_TEXT +
                            ", " + NoteTable.NOTE_TRANS_ID + ") VALUES (?, ?)");
            stmt.setString(1, note.getNoteText());
            stmt.setInt(2, note.getTransactionId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Unable to insert note.", e);
        }
    }

    @Override
    default void deleteNote(Note note) throws StorageException {
        try {
            PreparedStatement stmt = getDatabase().prepareStatement("DELETE FROM " + NoteTable.TABLE_NAME +
                    " WHERE " + NoteTable.NOTE_TRANS_ID + " = ?");
            stmt.setInt(1, note.getTransactionId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Unable to delete note.", e);
        }
    }

    @Override
    default void editNote(Note note) throws StorageException {

        try {
            PreparedStatement stmt =
                    getDatabase().prepareStatement("UPDATE " + NoteTable.TABLE_NAME +
                            " SET " + NoteTable.NOTE_TEXT + "=? WHERE " + NoteTable.NOTE_TRANS_ID + " = ?");

            stmt.setString(1, note.getNoteText());
            stmt.setInt(2, note.getTransactionId());
            stmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new StorageException("Unable to edit note.", e);
        }
    }

    @Override
    default List<Note> getAllNotes() throws StorageException {
        try {
            Statement stmt = getDatabase().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + NoteTable.NOTE_TRANS_ID +
                    ", " + NoteTable.NOTE_TEXT +
                    " FROM " + NoteTable.TABLE_NAME + ";");

            ArrayList<Note> notes = new ArrayList<>();

            while (rs.next()) {

                int note_trans_id = rs.getInt(NoteTable.NOTE_TRANS_ID);
                String note_text = rs.getString(NoteTable.NOTE_TEXT);

                Note currentNote = new Note(note_trans_id, note_text);

                notes.add(currentNote);
            }

            return notes;
        } catch (java.sql.SQLException e) {
            throw new StorageException("Error while getting all notes", e);
        }
    }
}
