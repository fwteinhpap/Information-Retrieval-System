package txtparsing;

public class MyDoc {
    int docID;
    String title;
    String content;

    public MyDoc(int docID, String title, String content) {
        this.docID = docID;
        this.title = title;
        this.content = content;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "MyDoc{" +
                "docID=" + docID +
                ", title='" + title + '\'' +

                '}';
    }

    public void setContent(String content) {
        this.content = content;
    }
}
