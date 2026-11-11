public enum LoteInscricao {
    PROMOCIONAL(0.20),
    LOTE_1(0.10),
    LOTE_2(0.00);

    private final double percentualDesconto;

    LoteInscricao(double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public double getPercentualDesconto() {
        return percentualDesconto;
    }
}