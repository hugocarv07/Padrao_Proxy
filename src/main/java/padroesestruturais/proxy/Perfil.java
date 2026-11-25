package padroesestruturais.proxy;

public enum Perfil {
    JUIZ(true),
    SERVIDOR(true),
    ADVOGADO(false),
    VISITANTE(false);

    private final boolean acessaSigiloso;

    Perfil(boolean acessaSigiloso) {
        this.acessaSigiloso = acessaSigiloso;
    }

    public boolean podeVerSigiloso() {
        return acessaSigiloso;
    }
}
