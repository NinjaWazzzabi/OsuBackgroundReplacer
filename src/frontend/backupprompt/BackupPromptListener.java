package frontend.backupprompt;

/**
 * Interface for {@link BackupPrompt} listeners
 */
public interface BackupPromptListener {
    /**
     * Called when the user chooses the "yes" option
     */
    void backupYes();

    /**
     * Called when the user chooses the "no" option
     */
    void backupNo();
}
