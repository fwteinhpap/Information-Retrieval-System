package txtparsing;

public class MyQuery {
    private int queryid;
    private String query;

    public MyQuery(int queryid, String query) {
        this.queryid = queryid;
        this.query = query;
    }

    @Override
    public String toString() {
        return "MyQuery{" +
                "queryid=" + queryid +
                ", query='" + query + '\'' +
                '}';
    }

    public int getQueryid() {
        return queryid;
    }

    public void setQueryid(int queryid) {
        this.queryid = queryid;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
