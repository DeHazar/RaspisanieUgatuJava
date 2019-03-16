package hazarsoftware.raspisanieugatu.NotesPart.data;

import android.support.annotation.Nullable;
import com.google.common.base.Objects;
import java.util.UUID;

public class Note
{
    private final String mId;
    @Nullable
    private final String mTitle;
    @Nullable
    private final String mDescription;


    public Note(@Nullable String title, @Nullable String description) {
        mId = UUID.randomUUID().toString();
        mTitle = title;
        mDescription = description;

    }

    public Note(@Nullable String id,@Nullable String title, @Nullable String description) {
        mId = id;
        mTitle = title;
        mDescription = description;

    }
    public String getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean isEmpty() {
        return (mTitle == null || "".equals(mTitle)) &&
                (mDescription == null || "".equals(mDescription));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equal(mId, note.mId) &&
                Objects.equal(mTitle, note.mTitle) &&
                Objects.equal(mDescription, note.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mDescription);
    }
}
