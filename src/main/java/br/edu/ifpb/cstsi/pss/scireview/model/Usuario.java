package br.edu.ifpb.cstsi.pss.scireview.model;

import br.edu.ifpb.cstsi.pss.scireview.exception.DadosInvalidosException;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class Usuario {

    private static final Pattern FORMATO_EMAIL = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final int TAMANHO_MINIMO_SENHA = 6;

    private final String email;
    private String senha;
    private String instituicao;
    private final Set<Papel> papeis;
    private final Set<AreaTematica> areasDeInteresse = new LinkedHashSet<>();

    public Usuario(String email, String senha, String instituicao, Set<Papel> papeis) {
        this.email = normalizarEmail(email);
        validarFormatoEmail(this.email);
        this.senha = validarSenha(senha);
        this.instituicao = validarInstituicao(instituicao);
        this.papeis = validarPapeis(papeis);
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = validarSenha(senha);
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = validarInstituicao(instituicao);
    }

    public Set<Papel> getPapeis() {
        return Collections.unmodifiableSet(papeis);
    }

    public boolean possuiPapel(Papel papel) {
        return papeis.contains(papel);
    }

    public Set<AreaTematica> getAreasDeInteresse() {
        return Collections.unmodifiableSet(areasDeInteresse);
    }

    public void adicionarAreaDeInteresse(AreaTematica area) {
        if (area == null) {
            throw new DadosInvalidosException("Área temática é obrigatória.");
        }
        areasDeInteresse.add(area);
    }

    public boolean possuiAreaDeInteresse(AreaTematica area) {
        return areasDeInteresse.contains(area);
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        Usuario usuario = (Usuario) objeto;
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    private static String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DadosInvalidosException("E-mail é obrigatório.");
        }
        return email.trim().toLowerCase();
    }

    private static void validarFormatoEmail(String email) {
        if (!FORMATO_EMAIL.matcher(email).matches()) {
            throw new DadosInvalidosException("E-mail inválido: " + email);
        }
    }

    private static String validarSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new DadosInvalidosException("Senha é obrigatória.");
        }
        if (senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new DadosInvalidosException("Senha deve ter no mínimo " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        return senha;
    }

    private static String validarInstituicao(String instituicao) {
        if (instituicao == null || instituicao.isBlank()) {
            throw new DadosInvalidosException("Instituição é obrigatória.");
        }
        return instituicao.trim();
    }

    private static Set<Papel> validarPapeis(Set<Papel> papeis) {
        if (papeis == null || papeis.isEmpty()) {
            throw new DadosInvalidosException("Usuário deve possuir ao menos um papel.");
        }
        return Set.copyOf(papeis);
    }
}
