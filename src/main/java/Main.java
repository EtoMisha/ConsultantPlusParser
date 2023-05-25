public class Main {
    public static void main(String[] args) throws Exception {
        String taxCodexPart1 = "https://www.consultant.ru/document/cons_doc_LAW_19671/";
        String taxCodexPart2 = "https://www.consultant.ru/document/cons_doc_LAW_28165/";
        String filename = "taxCodex.csv";

        ConsultantParser consultantParser = new ConsultantParser();
        consultantParser.parse(taxCodexPart1);
        consultantParser.parse(taxCodexPart2);
        consultantParser.writeToFile(filename);
    }
}
