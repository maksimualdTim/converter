CREATE TABLE Currencies (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Code VARCHAR(10) NOT NULL UNIQUE,
    FullName VARCHAR(100) NOT NULL,
    Sign VARCHAR(10) NOT NULL
);

CREATE TABLE ExchangeRates (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    BaseCurrencyId INT NOT NULL,
    TargetCurrencyId INT NOT NULL,
    Rate DECIMAL(20,6) NOT NULL,

    CONSTRAINT fk_exchange_rates_base_currency
        FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),

    CONSTRAINT fk_exchange_rates_target_currency
        FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID),

    CONSTRAINT uq_exchange_rate_pair
        UNIQUE (BaseCurrencyId, TargetCurrencyId),

    CONSTRAINT chk_different_currencies
        CHECK (BaseCurrencyId <> TargetCurrencyId),

    CONSTRAINT chk_positive_rate
        CHECK (Rate > 0)
);

ALTER TABLE Currencies
ADD CONSTRAINT uq_currencies_code UNIQUE (Code);

ALTER TABLE Currencies
ADD CONSTRAINT uq_currencies_sign UNIQUE (Sign);

INSERT INTO Currencies (Code, FullName, Sign) VALUES
('USD', 'US Dollar', '$'),
('EUR', 'Euro', '€'),
('RUB', 'Russian Ruble', '₽'),
('UZS', 'Uzbekistani Som', 'soʻm');

INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES
((SELECT ID FROM Currencies WHERE Code = 'USD'), (SELECT ID FROM Currencies WHERE Code = 'UZS'), 12850),
((SELECT ID FROM Currencies WHERE Code = 'EUR'), (SELECT ID FROM Currencies WHERE Code = 'UZS'), 13990),
((SELECT ID FROM Currencies WHERE Code = 'USD'), (SELECT ID FROM Currencies WHERE Code = 'RUB'), 93);