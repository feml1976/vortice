CREATE OR REPLACE PACKAGE LLANTAS."PK_MOVTMP"  AS 
  /* TODO enter package declarations (types, exceptions, methods etc) here */ 
  PROCEDURE LOGMOVTMP(movimientos VARCHAR2);
  PROCEDURE LOGMOVTMP(tick number, plac varchar2, klm number, usua varchar2, ofic number,movimientos VARCHAR2);
  PROCEDURE PERSISTENCIA(movimientos VARCHAR2);
  FUNCTION  FDB_PRUEBINS(pllanta varchar2, pgrupo char, pklm number, ppi number, ppc number, ppd number, ppresion number)  RETURN VARCHAR2;
  PROCEDURE PDB_INGRESOS(ptipo varchar2, pnombre varchar2, pnivel varchar2, pcalidad varchar2, psaga char, pdescripcion varchar2, pubicacion varchar2, pfoto blob);
END PK_MOVTMP;

CREATE OR REPLACE PACKAGE BODY LLANTAS."PK_MOVTMP"  AS
  PROCEDURE LOGMOVTMP(movimientos VARCHAR2) AS
  secu number(4);
  BEGIN
    select count(SECUENCIA) + 1 into secu from tmplogmov;
    insert into tmplogmov values(secu,null,movimientos,null,null,null,null,null,sysdate);
  END LOGMOVTMP;
  PROCEDURE LOGMOVTMP(tick number, plac varchar2, klm number, usua varchar2, ofic number,movimientos VARCHAR2) AS
  secu number(4);
  BEGIN
    select count(SECUENCIA) + 1 into secu from tmplogmov;
    insert into tmplogmov values(secu,tick,movimientos,plac,klm,'A',usua,ofic,sysdate);
  END LOGMOVTMP;
  PROCEDURE PERSISTENCIA(movimientos VARCHAR2) AS
  secu number(4);
  BEGIN
    select count(SECUENCIA) + 1 into secu from tmploglla;
    insert into tmploglla values(secu,movimientos,sysdate);
  END PERSISTENCIA;
FUNCTION  FDB_PRUEBINS(pllanta varchar2, pgrupo char, pklm number, ppi number, ppc number, ppd number, ppresion number)  RETURN VARCHAR2 IS
	BO_RESPUESTA_L     VARCHAR2(30);
BEGIN
  insert into muestreo values(pllanta,pgrupo,pklm,ppi,ppc,ppd,ppresion,sysdate);
  commit;
  select to_char(sysdate,'DDMMYYYY HH24:MI:SS') into BO_RESPUESTA_L from dual;
	  RETURN BO_RESPUESTA_L;
	  EXCEPTION WHEN OTHERS THEN
	  	BO_RESPUESTA_L := 'XX';
	  	RETURN BO_RESPUESTA_L;
END FDB_PRUEBINS;
PROCEDURE PDB_INGRESOS(ptipo varchar2, pnombre varchar2, pnivel varchar2, pcalidad varchar2, psaga char, pdescripcion varchar2, pubicacion varchar2, pfoto blob) AS
BEGIN
  INSERT INTO apex_krow(secuencia,tipo,estado,fecha,nombre,nivel,calidad,saga,descripcion,archivo) 
  VALUES (SEC_APEX_KROW.nextval,ptipo,'A', sysdate, pnombre, pnivel, pcalidad, psaga, pdescripcion, pfoto);
END PDB_INGRESOS;
END PK_MOVTMP;



CREATE OR REPLACE PROCEDURE LLANTAS.PDB_LEERLOG  
    (PAR_TICKET_E NUMBER, PAR_ERROR_S OUT VARCHAR) IS
L_LOGMOV_V2               TMPLOGMOV.LOGMOV%TYPE;
L_PLACA_V2                                                    VARCHAR2(500);
L_DORIGEN_V2                                              VARCHAR2(500);
L_DPDESTINO_V2                          VARCHAR2(500);
L_LLANTA_V2                                                  VARCHAR2(500);
L_GRUPO_V2                                                  VARCHAR2(500);
L_PI_V2                                                                                             VARCHAR2(500);
L_PC_V2                                                                                            VARCHAR2(500);
L_PD_V2                                                                                           VARCHAR2(500);
L_PRESION_V2                                VARCHAR2(500);
L_OBSERVACION_V2    VARCHAR2(500);
L_TRANSACCION_V2    VARCHAR2(500);
V2_ERROR_L        VARCHAR2(1000);
CURSOR LOG IS
                SELECT  SECUENCIA,TICKET,OFICINA,KLMS, PLACA,LOGMOV
                FROM TMPLOGMOV
                WHERE ESTADO = 'A'
                AND TICKET = PAR_TICKET_E;
                L_LONG_NB                      NUMBER:=0;
                L_POSICION_NB                             NUMBER:=0;
                CUANTOS                           NUMBER:=0;
BEGIN
                FOR A IN LOG
                LOOP
                               L_LOGMOV_V2 := A.LOGMOV;
                               L_LONG_NB := LENGTH(L_LOGMOV_V2);
                               WHILE (L_LONG_NB > 0)
                               LOOP
                                               --ELIMINA EL PRIMER CARACTER @
                                               IF SUBSTR(L_LOGMOV_V2,1,1) = '@' THEN
                                                                              L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,2,L_LONG_NB);
                                               END IF;
                                               -- EL ORIGEN
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,'@',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_DORIGEN_V2 := SUBSTR(L_LOGMOV_V2,3,L_POSICION_NB-3);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
                                               -- EL DESTINO
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,'@',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_DPDESTINO_V2 := SUBSTR(L_LOGMOV_V2,3,L_POSICION_NB-3);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
                                               -- LA LLANTA
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,'@',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_LLANTA_V2 := SUBSTR(L_LOGMOV_V2,1,L_POSICION_NB-1);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
                                               -- EL GRUPO
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,'@',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_GRUPO_V2 := SUBSTR(L_LOGMOV_V2,1,L_POSICION_NB-1);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
                                               -- LA PROFUNDIDA IZQUIERDA
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,'@',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_PI_V2 := SUBSTR(L_LOGMOV_V2,1,L_POSICION_NB-1);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
                                               -- LA PROFUNIDAD CENTRAL
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,'@',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_PC_V2 := SUBSTR(L_LOGMOV_V2,1,L_POSICION_NB-1);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
                                               -- LA PROFUNDIDA DERECHA
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,'@',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_PD_V2 := SUBSTR(L_LOGMOV_V2,1,L_POSICION_NB-1);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
                                               -- LA PRESION
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,'@',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_PRESION_V2 := SUBSTR(L_LOGMOV_V2,1,L_POSICION_NB-1);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
                                               -- OBSERVACION
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,'@',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_OBSERVACION_V2 := SUBSTR(L_LOGMOV_V2,1,L_POSICION_NB-1);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
                                               -- LA TRANSACCION
                                               L_POSICION_NB := INSTR(L_LOGMOV_V2,';',1,1);
                                               IF L_POSICION_NB = 0 THEN 
                                               -- El REGISTRO ESTA MAL
                                                               EXIT;
                                               END IF;
                                               L_TRANSACCION_V2 := SUBSTR(L_LOGMOV_V2,1,L_POSICION_NB-1);
                                               L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,L_POSICION_NB+1);
--DBMS_OUTPUT.PUT_LINE(L_TRANSACCION_V2||'-'||L_LLANTA_V2||'-'|| L_GRUPO_V2||'-'||L_PI_V2||'-'|| L_PC_V2||'-'||L_PD_V2||'-'|| L_DPDESTINO_V2||'-'||A.KLMS||'-'||L_PRESION_V2);
                                               IF L_TRANSACCION_V2 = 'M' THEN  --ES UNA TRANSACCION DE MONTAR UNA LLANTA
                                                               PK_LLANTASWEB.PDB_MONTARLLANTA(A.PLACA,L_LLANTA_V2, L_GRUPO_V2,L_PI_V2, L_PC_V2, L_PD_V2, L_DPDESTINO_V2,A.KLMS, SYSDATE,L_PRESION_V2, PAR_ERROR_S);
                    select count(SECUENCIA_NB)+1 into SECLOGMOV from logweb
                    where TICKET_NB=A.TICKET
                    and PLACA_v2=A.PLACA;
                    if PAR_ERROR_S = 'ERROR EN PDB_MONTARLLANTA' then
                      select count(SECUENCIA_NB)+1 into SECLOGMOV from logweb
                      where TICKET_NB=A.TICKET
                      and PLACA_v2=A.PLACA;
                      insert into logweb (SECUENCIA_NB,SECTMPLOGMOV,TICKET_NB,PLACA_v2,ESTADO_V2,FECHA_DT,MENSAJE_V2,LLANTA_V2,GRUPO_V2,PI_V2,PC_V2,PD_V2,DPDESTINO_V2,KLMS_V2,PRESION_V2)
                      values(SECLOGMOV,a.SECUENCIA,a.TICKET,a.PLACA,'R',sysdate,'ERROR EN PK_LLANTASWEB.PDB_MONTARLLANTA',L_LLANTA_V2, L_GRUPO_V2,L_PI_V2, L_PC_V2, L_PD_V2, L_DPDESTINO_V2,A.KLMS,L_PRESION_V2);
                      commit;
                    end if;
                                               ELSIF L_TRANSACCION_V2 = 'R' THEN  --ES UNA TRANSACCION DE DESMONTAR UNA LLANTA                                                               
                                                               PK_LLANTASWEB.PDB_DESMONTARLLANTA(A.PLACA, L_LLANTA_V2,L_GRUPO_V2,1,A.KLMS,SYSDATE, PAR_ERROR_S);
                    select count(SECUENCIA_NB)+1 into SECLOGMOV from logweb
                    where TICKET_NB=A.TICKET
                    and PLACA_v2=A.PLACA;
                    if PAR_ERROR_S = 'ERROR EN PDB_MONTARLLANTA' then
                      select count(SECUENCIA_NB)+1 into SECLOGMOV from logweb
                      where TICKET_NB=A.TICKET
                      and PLACA_v2=A.PLACA;
                      insert into logweb (SECUENCIA_NB,SECTMPLOGMOV,TICKET_NB,PLACA_v2,ESTADO_V2,FECHA_DT,MENSAJE_V2,LLANTA_V2,GRUPO_V2,PI_V2,PC_V2,PD_V2,DPDESTINO_V2,KLMS_V2,PRESION_V2)
                      values(SECLOGMOV,a.SECUENCIA,a.TICKET,a.PLACA,'R',sysdate,'ERROR EN PK_LLANTASWEB.PDB_DESMONTARLLANTA',L_LLANTA_V2, L_GRUPO_V2,NULL, NULL, NULL, NULL,A.KLMS,NULL);
                      commit;
                    end if;
                                               ELSIF L_TRANSACCION_V2 = 'T' THEN  --ES UNA TRANSACCION DE ROTAR UNA LLANTA
                                                               PK_LLANTASWEB.PDB_ROTARLLANTA(A.PLACA, L_LLANTA_V2,L_GRUPO_V2,L_DPDESTINO_V2, PAR_ERROR_S);
                    select count(SECUENCIA_NB)+1 into SECLOGMOV from logweb
                    where TICKET_NB=A.TICKET
                    and PLACA_v2=A.PLACA;
                    if PAR_ERROR_S = 'ERROR EN PDB_MONTARLLANTA' then
                      select count(SECUENCIA_NB)+1 into SECLOGMOV from logweb
                      where TICKET_NB=A.TICKET
                      and PLACA_v2=A.PLACA;
                      insert into logweb (SECUENCIA_NB,SECTMPLOGMOV,TICKET_NB,PLACA_v2,ESTADO_V2,FECHA_DT,MENSAJE_V2,LLANTA_V2,GRUPO_V2,PI_V2,PC_V2,PD_V2,DPDESTINO_V2,KLMS_V2,PRESION_V2)
                      values(SECLOGMOV,a.SECUENCIA,a.TICKET,a.PLACA,'R',sysdate,'ERROR EN PK_LLANTASWEB.PDB_ROTARLLANTA',L_LLANTA_V2, L_GRUPO_V2,NULL, NULL, NULL, L_DPDESTINO_V2,NULL,NULL);
                      commit;
                    end if;
                                               END IF;
                                               --ELIMINA CARACTER ; FIN DE DE TRANSACCION
                                               IF SUBSTR(L_LOGMOV_V2,1,1) = ';' THEN
                                                                              L_LOGMOV_V2 := SUBSTR(L_LOGMOV_V2,2,L_LONG_NB);
                                               END IF;
                                               L_LONG_NB := LENGTH(L_LOGMOV_V2);
--dbms_output.put_line('L_LONG_NB : '||L_LONG_NB);          
                               END LOOP;
                END LOOP;
                                               --ACTUALIZAR EL REGISTRO QUE SE ACABA DE PROCESAR
                                               UPDATE TMPLOGMOV
                                               SET ESTADO = 'P'
                                               WHERE ESTADO = 'A'
                                               AND TICKET = PAR_TICKET_E
                                               AND OFICINA = A.OFICINA;
                                              COMMIT;
END;



