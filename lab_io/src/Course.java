public record Course(String code, String title, int price) {

    // Одна строка текстового каталога: код;название;цена
    public String toLine() {
        return code + ";" + title + ";" + price;
    }
}
