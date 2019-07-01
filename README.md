[![Build Status](https://travis-ci.com/navikt/data-catalog-policies.svg?branch=master)](https://travis-ci.com/navikt/data-catalog-policies)

# data-catalog-policies
Applikasjonen er en del av Datakatalog - prosjektet som skal levere en katalog over datasett i NAV
, til hvilke formål disse datasettene brukes, og hvilket rettslig grunnlag som ligger til grunn for bruken.

Applikasjonen data-catalog-policies samler all funksjonalitet knyttet til Behandlingsregler for et datasett. 
En behandlingsregel består av et datasett, et formål og et rettslig grunnlag. Det kan være ett eller flere formål 
knyttet til et datasett.

## Kom i gang
Prosjektet krever maven og java 11

For å bygge

``mvn clean install``

For å kjøre, navigèr til ``data-catalog-policies-app`` og kjør

``mvn exec:java -Dspring.profiles.active=test``

Swagger-dokumentasjon av tjenestene er tilgjenglig på
http://localhost:8080/swagger-ui.html
