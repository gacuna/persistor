--Depositos
INSERT INTO DEPOSITO (ID, ESTADO, PRIORIDADFORZADA, TIPOOPERATORIA)
VALUES (1, 'VALIDAR_CMC7', NULL, 'AL_COBRO');
--Cheques
INSERT INTO CHEQUE (ID, ACTIVO, CODBANCO, CODCHEQUE, CODCUENTA, NUMERO, CODFILIAL, CODPOSTAL, ESTADO, IMPORTE, FECHAINGRESO, DEPOSITO_ID)
VALUES (1,'1','072','09251690','9000', 729251690900042,'042','1003','VALIDAR_CMC',2880.00,'2019-04-24',1);
--Config
INSERT INTO GUVCONFIG (ID, VALOR) VALUES ('IMPORTE_TRUCAMIENTO', '40000');
INSERT INTO GUVCONFIG (ID, VALOR) VALUES ('DIAS_VALIDACION_CMC7_DUPLICADOS', '7');
