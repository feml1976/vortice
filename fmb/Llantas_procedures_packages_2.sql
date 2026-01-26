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




CREATE OR REPLACE PACKAGE LLANTAS."PK_LLANTASWEB" AS
   /* Procedimiento para montar llantas a un vehículo'*/
   PROCEDURE PDB_MONTARLLANTA(par_vehiculo_e CHAR, par_llanta_e VARCHAR, par_grupo_e CHAR,
   par_profi_e NUMBER,par_profc_e NUMBER, par_profd_e NUMBER, par_posicion_e NUMBER,
   par_kilomi_e NUMBER, par_fechai_e DATE, par_presion_e NUMBER,par_retorno_s OUT VARCHAR );
   /* Procedimiento para desmontar llantas a un vehículo'*/
   PROCEDURE PDB_DESMONTARLLANTA(par_vehiculo_e CHAR, par_llanta_e VARCHAR, par_grupo_e CHAR,
   par_observacion_e NUMBER,par_kilomi_e NUMBER, par_fechai_e DATE,  par_retorno_s OUT VARCHAR);
   /* Procedimiento para rotar llantas a un vehículo'*/
   PROCEDURE PDB_ROTARLLANTA(par_vehiculo_e CHAR, par_llanta_e VARCHAR, par_grupo_e CHAR,
   par_posicion_e NUMBER, par_retorno_s OUT VARCHAR);
   /*Función para validar una llanta en el inventario */
   FUNCTION FDB_VALIDALLANTAINV (PAR_LLANTA VARCHAR) RETURN DECIMAL;
   /*Función para retornar el grupo de una llanta del inventario*/
   FUNCTION  FDB_DATOSLLANTA (PAR_LLANTA VARCHAR) RETURN SYS_REFCURSOR;
   /*Función para retornar las llanta montadas en un vehículo*/   
   FUNCTION FDB_LLANTASXVEHI(PAR_PLACA_E CHAR) RETURN SYS_REFCURSOR;
   /*Función para retornar las llantas disponibles en intermedio para recircular*/    
   FUNCTION FDB_LLANTAS_INTERMEDIO RETURN SYS_REFCURSOR;
  /*Procedimiento para recircular las llantas disponibles en intermedio*/   
   PROCEDURE PDB_RECIRCULAR (PAR_LLANTA_E VARCHAR, PAR_GRUPO_E CHAR, PAR_TIPO_E CHAR, PAR_INV_E NUMBER, par_retorno_s OUT VARCHAR);
  /*Función para retornar las profundidades de una ficha técnica*/    
   FUNCTION FDB_PROFUNDIDAD_FICHA(PAR_LLANTA_E VARCHAR) RETURN SYS_REFCURSOR;
  /*Funcion para devolver los datos de la placa bien sea de un trailer o un vehiculo*/
   FUNCTION FDB_DATOS_PLACA(PLACA VARCHAR) RETURN SYS_REFCURSOR;
END PK_LLANTASWEB;

CREATE OR REPLACE PACKAGE BODY LLANTAS."PK_LLANTASWEB" IS
  /*PROCEDIMIENTO PARA MONTAR UNA LLANTA*/
  PROCEDURE PDB_MONTARLLANTA(par_vehiculo_e CHAR, par_llanta_e VARCHAR, par_grupo_e CHAR,
  par_profi_e NUMBER,par_profc_e NUMBER, par_profd_e NUMBER, par_posicion_e NUMBER,
  par_kilomi_e NUMBER, par_fechai_e DATE, par_presion_e NUMBER, par_retorno_s OUT VARCHAR) as
  nb_valor_l      NUMBER(14,4);
  dt_fecha_l      DATE;
  nb_provee_l     NUMBER(20);
  nb_factura_l    NUMBER(20);
  nb_ficha_l      NUMBER(10);
  ch_grupo_l      CHAR(4);
  nb_pi_l         NUMBER(3,1);
  nb_pc_l         NUMBER(3,1);
  nb_pd_l         NUMBER(3,1);
  lv_sqlcode      NUMBER; /* Place to hold SQLCODE */
  lv_sqlerrm      VARCHAR2(500); /* Place to hold SQLERRM */
  v2_otro_l       VARCHAR2(1000);
    cursor c_llantas is
      --Selecciona los datos de la llanta
      SELECT valor, fecha, prove, factura, ficha, grupo, pi,pc, pd
      FROM inventario, fichatec
      WHERE llanta = par_llanta_e
      AND ficha = codigo;
  BEGIN
  par_retorno_s := null;
--dbms_output.put_line('par_llanta_e, -codigo: '||par_llanta_e);
    v2_otro_l := 'Linea 31';
    OPEN c_llantas;
    --LOOP nmvl (Esto no va) must to put ok y kow.
      FETCH c_llantas INTO nb_valor_l, dt_fecha_l, nb_provee_l, nb_factura_l,nb_ficha_l, ch_grupo_l,
      nb_pi_l,nb_pc_l,nb_pd_l;
      IF c_llantas%NOTFOUND THEN --QUIERE DECIR QUE ES UNA TRANSACCION DE ROTAR Y ESTA EN INTERMEDIO
--dbms_output.put_line('Antes SELECT del not found');
        v2_otro_l := 'Linea 39';
        --Selecciona los datos de retiradas porque se acaba de desmontar la llanta y es un rotar
        SELECT b.valor, b.fecha, b.provee, b.factura, b.ficha, b.grupo, c.pi,c.pc, c.pd
        INTO nb_valor_l, dt_fecha_l, nb_provee_l, nb_factura_l,nb_ficha_l, ch_grupo_l,nb_pi_l,nb_pc_l,nb_pd_l
        FROM intermedio a , historia b, fichatec c
        WHERE a.llanta = par_llanta_e
        AND a.grupo = par_grupo_e
        AND a. llanta = b.llanta
        AND a.grupo = b.grupo
        AND a.ficha = c.codigo;
        v2_otro_l := 'Linea 48';    
--dbms_output.put_line('Insertando_ '||par_llanta_e||'--'||par_grupo_e);
        /* Inserta la llanta */
        insert into llantas
        values(par_llanta_e, par_grupo_e, nb_valor_l, dt_fecha_l, nb_provee_l,
        nb_factura_l, nb_ficha_l, 0,0,0,0, par_vehiculo_e, par_posicion_e, par_kilomi_e,par_fechai_e);
        v2_otro_l := 'Linea 55';
--dbms_output.put_line('uno');
        /* Borra de intermedio la llanta */
        delete from intermedio
        where llanta = par_llanta_e
        and grupo = par_grupo_e;
        v2_otro_l := 'Linea 61';      
      ELSIF c_llantas%FOUND THEN --QUIERE DECIR QUE ES UNA TRANSACCION DE MONTAR Y ESTA EN INVENTARIO
        v2_otro_l := 'Linea 63';
        /* Inserta la llanta */
        insert into llantas
        values(par_llanta_e, par_grupo_e, nb_valor_l, dt_fecha_l, nb_provee_l,
        nb_factura_l, nb_ficha_l, 0,0,0,0, par_vehiculo_e, par_posicion_e, par_kilomi_e,par_fechai_e);
        v2_otro_l := 'Linea 68';
        /* Borra de inventario la llanta */
        delete from inventario
        where llanta = par_llanta_e
        and grupo = par_grupo_e;
        v2_otro_l := 'Linea 73';
        IF (SUBSTR(par_grupo_e,1,2) <> '00') THEN --Valida que la llanta no es nueva
           nb_pi_l:= par_profi_e;
           nb_pc_l:= par_profc_e;
           nb_pd_l:= par_profd_e;
        END IF;   
        v2_otro_l := 'Linea 79';
        /*Inserta el primer muestreo de la llanta en ese vehículo*/
        insert into muestreo
        values(par_llanta_e, par_grupo_e, par_kilomi_e, par_profi_e, par_profc_e, par_profd_e, par_presion_e, par_fechai_e);           
        v2_otro_l := 'Linea 83';
      END IF;
    --END LOOP;  nmvl (Esto no va) must to put ok y kow.
  CLOSE c_llantas;
  EXCEPTION
    WHEN OTHERS THEN
       lv_sqlcode := SQLCODE;
       lv_sqlerrm := SQLERRM;
       v2_otro_l := v2_otro_l||'-'||lv_sqlcode||'-'||lv_sqlerrm||' PAR: '||par_vehiculo_e||','||par_llanta_e||','||par_grupo_e||','||par_profi_e||','||par_profc_e||','||par_profd_e||','||par_posicion_e||','||par_kilomi_e||','||par_fechai_e||','||par_presion_e;
       INSERT INTO log_llantas
       VALUES (TO_NUMBER(SQ_LOG_LLANTAS.nextval),'ERROR EN PDB_MONTARLLANTA '||' '||V2_OTRO_L,SYSDATE);
       par_retorno_s   := 'ERROR EN PDB_MONTARLLANTA';       
  END;
  /*PROCEDIMIENTO PARA DESMONTAR LLANTAS*/
  PROCEDURE PDB_DESMONTARLLANTA(par_vehiculo_e CHAR, par_llanta_e VARCHAR, par_grupo_e CHAR,par_observacion_e NUMBER, par_kilomi_e NUMBER,
  par_fechai_e DATE,  par_retorno_s OUT VARCHAR) AS
  nb_kmsrecorrido_l    number(14,4);
  nb_kmsinstala_l      number(14,4);
  nb_cuenta_l          NUMBER(5); 
  nb_secuencia_l       NUMBER(20);
  NB_CUENTAH_L         NUMBER(5);
  lv_sqlcode           NUMBER; /* Place to hold SQLCODE */
  lv_sqlerrm           VARCHAR2(500); /* Place to hold SQLERRM */
  v2_otro_l            VARCHAR2(1000);
  CURSOR C_LLANTA (par_llan_e VARCHAR, par_gru_e CHAR)IS
    SELECT *
    FROM llantas
    WHERE llanta = par_llan_e
    AND grupo = par_gru_e;
  CURSOR C_MUESTREO IS
    SELECT *
    FROM muestreo
    WHERE llanta = par_llanta_e
    AND grupo = par_grupo_e;
  BEGIN
   -- Crea la llanta retirada en historia  y la pasa a intermedio
   FOR rec_llantas IN c_llanta (par_llanta_e, par_grupo_e) LOOP     
        --VALIDA QUE LA LLANTA YA ESTA EN HISTORIA
      SELECT COUNT(*)
      INTO NB_CUENTAH_L
      FROM HISTORIA
      WHERE LLANTA = PAR_LLANTA_E
      AND GRUPO = PAR_GRUPO_E;
      v2_otro_l := 'Error en linea 129';
      IF (NB_CUENTAH_L = 0) THEN
            INSERT INTO historia
            VALUES (rec_llantas.llanta, rec_llantas.grupo, rec_llantas.valor, rec_llantas.fecha, rec_llantas.provee, rec_llantas.factura, 
            rec_llantas.ficha,rec_llantas.neuma, rec_llantas.valorrn, rec_llantas.protec, rec_llantas.valorp, rec_llantas.vehiculo, rec_llantas.posicion, 
            rec_llantas.kinstala, rec_llantas.fechai, par_kilomi_e, par_fechai_e,par_observacion_e);    
            v2_otro_l := 'Error en linea 135';
            --BORRAR LOS MUESTROES EN ESTA SECCION            
            DELETE FROM muestreo
            WHERE llanta = par_llanta_e
            AND grupo = par_grupo_e;
            v2_otro_l := 'Error en linea 140';
      END IF;       
      nb_kmsinstala_l := rec_llantas.kinstala;
      INSERT INTO intermedio
      VALUES(rec_llantas.llanta, rec_llantas.grupo, 1, null, rec_llantas.ficha);     
      v2_otro_l := 'Error en linea 145';
    END LOOP;
    nb_kmsrecorrido_l := par_kilomi_e - nb_kmsinstala_l;
    SELECT count(*)
    INTO nb_cuenta_l
    FROM kms_recorrido_llantas
    WHERE kmrl_llanta_nb = par_llanta_e
    AND SUBSTR(kmrl_grupo_ch,3,1) = SUBSTR(par_grupo_e,3,1);
    v2_otro_l := 'Error en linea 155';
    IF (nb_cuenta_l = 0) THEN --Valida que no tiene kms recorrido el reporte
      SELECT MAX(kmrl_secuencia_nb) + 1
      INTO nb_secuencia_l
      FROM kms_recorrido_llantas;
      v2_otro_l := 'Error en linea 161';
      --if (nb_kmsrecorrido_l < 0 OR nb_kmsrecorrido_l IS NULL) THEN
      --   raise VALUE_ERROR;
      --else
          INSERT INTO kms_recorrido_llantas
          VALUES (nb_secuencia_l, par_llanta_e, par_grupo_e, nb_kmsrecorrido_l,sysdate);
          v2_otro_l := 'Error en linea 165';
      --end if;
    ELSIF (nb_cuenta_l > 0) THEN --Valida que si tiene kms recorrido el reporte  
      --Actualiza el kilometraje recorrido de la llanta
      UPDATE kms_recorrido_llantas
      SET kmrl_kmsrecorrido_nb = kmrl_kmsrecorrido_nb + nb_kmsrecorrido_l,
      kmrl_fecha_dt = sysdate
      WHERE kmrl_llanta_nb = par_llanta_e
      AND SUBSTR(kmrl_grupo_ch,3,1) = SUBSTR(par_grupo_e,3,1);
      v2_otro_l := 'Error en linea 173';
    END IF;  
  /* Borra la vieja en llantas */
    DELETE FROM llantas
    WHERE llanta = par_llanta_e 
    AND grupo = par_grupo_e;
    v2_otro_l := 'Error en linea 180';
    IF (NB_CUENTAH_L = 0) THEN
      FOR REC_MUESTREO IN C_MUESTREO LOOP
       INSERT INTO histomues
       VALUES (rec_muestreo.llanta, rec_muestreo.grupo, rec_muestreo.kilom, rec_muestreo.pi,
       rec_muestreo.pc, rec_muestreo.pd, rec_muestreo.presion, rec_muestreo.fecha);
       v2_otro_l := 'Error en linea 187';
       /*DELETE FROM muestreo
       WHERE llanta = par_llanta_e
       AND grupo = par_grupo_e;*/
      END LOOP;           
    END IF;
    EXCEPTION
    WHEN OTHERS THEN
       rollback;
       lv_sqlcode := SQLCODE;
       lv_sqlerrm := SQLERRM;
       v2_otro_l := v2_otro_l||'-'||lv_sqlcode||'-'||lv_sqlerrm||' PAR: '||par_vehiculo_e||','||par_llanta_e||','||par_grupo_e||','||par_observacion_e||','||par_kilomi_e||','||par_fechai_e;
       INSERT INTO log_llantas
       VALUES (TO_NUMBER(SQ_LOG_LLANTAS.nextval),'ERROR EN PDB_DESMONTARLLANTA '||' '||V2_OTRO_L,SYSDATE);
       par_retorno_s   := 'ERROR EN PDB_DESMONTARLLANTA';    
  END;
  /*PROCEDIMIENTO PARA ROTAR LLANTAS*/
  PROCEDURE PDB_ROTARLLANTA(par_vehiculo_e CHAR, par_llanta_e VARCHAR, par_grupo_e CHAR,par_posicion_e NUMBER, par_retorno_s OUT VARCHAR) AS
  lv_sqlcode      NUMBER; /* Place to hold SQLCODE */
  lv_sqlerrm      VARCHAR2(500); /* Place to hold SQLERRM */
  v2_otro_l       VARCHAR2(1000);    
  BEGIN
    UPDATE llantas
    SET posicion = par_posicion_e
    WHERE llanta = par_llanta_e 
    AND grupo = par_grupo_e;
    v2_otro_l := 'Error en línea 218';
    EXCEPTION
    WHEN OTHERS THEN
       rollback;
       lv_sqlcode := SQLCODE;
       lv_sqlerrm := SQLERRM;
       v2_otro_l := v2_otro_l||'-'||lv_sqlcode||'-'||lv_sqlerrm||' PAR: '||par_vehiculo_e||','||par_llanta_e||','||par_grupo_e||','||par_posicion_e;
       INSERT INTO log_llantas
       VALUES (TO_NUMBER(SQ_LOG_LLANTAS.nextval),'ERROR EN PDB_ROTARLLANTA'||' '||V2_OTRO_L,SYSDATE);
       par_retorno_s   := 'ERROR EN PDB_ROTARLLANTA';  
  END;
  /*FUNCION PARA VALIDAR UNA LLANTA EN EL INVENTARIO */
  FUNCTION         FDB_VALIDALLANTAINV (PAR_LLANTA VARCHAR) RETURN DECIMAL IS
  NB_RESPUESTA_L DECIMAL;
  NB_CUENTA_L    NUMBER(5);
  BEGIN
    --CONSULTA QUE LA LLANTA INGRESADA ESTE EN EL INVENTARIO
    SELECT count(*)
    INTO NB_CUENTA_L
    FROM LLANTAS.INVENTARIO
    WHERE llanta = par_llanta;    
    IF NB_CUENTA_L > 0 THEN --Valida que la llanta existe en inventario
       NB_RESPUESTA_L := 1 ;
    ELSE
       NB_RESPUESTA_L := 0; --Valida que la llanta no existe en inventario
    END IF;
    RETURN NB_RESPUESTA_L;
    EXCEPTION WHEN OTHERS THEN
      NB_RESPUESTA_L := 99;
      RETURN NB_RESPUESTA_L;
   END;
  /*FUNCION PARA RETORNAR EL GRUPO DE UNA LLANTA DEL INVENTARIO */
  FUNCTION  FDB_DATOSLLANTA (PAR_LLANTA VARCHAR) RETURN SYS_REFCURSOR AS
  l_cursor sys_refcursor;
  BEGIN
    OPEN l_cursor FOR
    --CONSULTA QUE LA LLANTA INGRESADA ESTE EN EL INVENTARIO    
      SELECT GRUPO, pi,pc,pd
      FROM INVENTARIO, fichatec
      WHERE llanta = par_llanta
      AND ficha = codigo;
      RETURN l_cursor;
  END;    
  /*FUNCION PARA RETORNAR LAS LLANTAS MONTADAS EN UN VEHICULO*/   
  FUNCTION FDB_LLANTASXVEHI(PAR_PLACA_E CHAR) RETURN SYS_REFCURSOR AS
  l_cursor sys_refcursor;
  BEGIN
  OPEN l_cursor FOR
    SELECT c.vehiculo, C.llanta, C.grupo, C.posicion, FECHAM, PROFIM, PROFICM, PROFDM, PRESIONM, KMSM
    FROM llantas C, (SELECT a.llanta LLANTAM, a.grupo GRUPOM, a.kilom KMSM, a.fecha FECHAM, a.pi PROFIM, a.pc PROFICM, a.pd PROFDM, a.presion PRESIONM
             FROM muestreo a
              WHERE a.fecha = (SELECT MAX(b.fecha)
                               FROM  muestreo b
                               WHERE a.llanta = b.llanta
                                 AND SUBSTR(a.grupo,3,1) = SUBSTR(b.grupo,3,1))) MUESTREO
    WHERE C.vehiculo = par_placa_e
    AND C.LLANTA = LLANTAM (+)
    AND C.GRUPO = GRUPOM (+)
    ORDER BY C.posicion;
  RETURN l_cursor;
  END;
  /*FUNCION PARA RETORNAR LAS LLANTAS DISPONIBLES EN INTERMEDIO PARA RECIRCULAR*/   
  FUNCTION FDB_LLANTAS_INTERMEDIO RETURN SYS_REFCURSOR AS
  l_cursor sys_refcursor;
  BEGIN
  OPEN l_cursor FOR
    SELECT llanta LLANTA, grupo GRP, estado ESTADO, prove PROVEE, ficha FICHA
    FROM intermedio
    ORDER BY llanta, grupo;
  RETURN l_cursor;
  END;
  /*PROCEDIMIENTO RECIRUCLAR */   
  PROCEDURE PDB_RECIRCULAR (PAR_LLANTA_E VARCHAR, PAR_GRUPO_E CHAR, PAR_TIPO_E CHAR, PAR_INV_E NUMBER, par_retorno_s OUT VARCHAR) AS
  llantaval       inventario.llanta%type;
  NuevoGrupo      inventario.grupo%type;
  LaFicha         inventario.ficha%type;
  nb_prov_l       number(5);
  nb_fac_l        number(7);
  loco            char(2);
  locos           char(2);
  locura          number(1);
  sale            exception;
  PriDigi         number(2);
  SegDigi         number(1);
  nb_secuencia_l  kms_recorrido_llantas.kmrl_secuencia_nb%TYPE; --Variable para la secuencia de la llanta
  lv_sqlcode      NUMBER; /* Place to hold SQLCODE */
  lv_sqlerrm      VARCHAR2(500); /* Place to hold SQLERRM */
  v2_otro_l       VARCHAR2(1000); 
    --Cursor para asegurar la transacción verificando que la llanta esta aún en intermedio
     CURSOR valida IS
      SELECT llanta, grupo, ficha
      FROM intermedio
      WHERE llanta = par_llanta_e;
    --Cursor para consultar la historia de la llanta  
     CURSOR historia is 
       SELECT provee, factura
       FROM historia
       WHERE llanta = par_llanta_e
       AND grupo = par_grupo_e;
  BEGIN
    llantaval := 0;
    OPEN valida;
    FETCH valida
      INTO llantaval, NuevoGrupo, LaFicha;
    IF (valida%NOTFOUND) THEN
    v2_otro_l := 'Error en linea 335';
       close valida;
       raise sale;
    END IF;
    CLOSE valida;
    v2_otro_l := 'Error en linea 340';
    PriDigi := to_number(substr(NuevoGrupo, 1, 2));
    SegDigi := to_number(substr(NuevoGrupo, 3, 1));
    v2_otro_l := 'Error en linea 343';
    IF (PAR_TIPO_E = 'R') THEN  --Valida que es un reecauche
      SegDigi := SegDigi + 1;
      loco := '00';
      v2_otro_l := 'Error en linea 347';
    ELSIF (PAR_TIPO_E = 'G') THEN --Valida que es un gallo
      PriDigi := PriDigi + 1;
      IF (PriDigi <= 9) THEN
        locos := '0'||to_char(PriDigi);
      ELSIF (PriDigi > 9) THEN
        locos := PriDigi;
      END IF;
      locura :=  to_number(substr(NuevoGrupo, 3, 1));
    END IF;
    v2_otro_l := 'Error en linea 357';
    if PAR_TIPO_E = 'R' then --Valida que seleccionó reencauche
      NuevoGrupo := (loco||to_char(SegDigi));
      --Selecciona la máxima secuencia de kms_recorrido_llantas
      SELECT NVL(MAX(kmrl_secuencia_nb),0) + 1
      INTO nb_secuencia_l
      FROM kms_recorrido_llantas;
      v2_otro_l := 'Error en linea 365';  
      --Inserta el kilometraje de la nueva llanta reencauchada
      INSERT INTO kms_recorrido_llantas
      VALUES (nb_secuencia_l, par_llanta_e, par_grupo_e, 0,sysdate);
      v2_otro_l := 'Error en linea 369';
      nb_secuencia_l := nb_secuencia_l + 1;
    else --Valida que seleccionó gallo
      NuevoGrupo := (locos||to_char(locura));
      --Consulta la información de la factura
      OPEN historia;
      FETCH historia INTO nb_prov_l,nb_fac_l;
      IF (historia%NOTFOUND) THEN
            v2_otro_l := 'Error en linea 377';
       close historia;
       raise sale;
      END IF;
      CLOSE historia;
    end if;
    /*   Salva la transaccion   */
    INSERT INTO inventario
    VALUES (par_llanta_e,nuevogrupo,PAR_INV_E, 0, sysdate,nb_prov_l,nb_fac_l, laficha);
    v2_otro_l := 'Error en linea 387';
    DELETE FROM intermedio
    WHERE llanta = par_llanta_e;
    v2_otro_l := 'Error en linea 391';
  EXCEPTION
    WHEN OTHERS THEN
       rollback;
       lv_sqlcode := SQLCODE;
       lv_sqlerrm := SQLERRM;
       v2_otro_l := v2_otro_l||'-'||lv_sqlcode||'-'||lv_sqlerrm||' PAR: '||PAR_LLANTA_E||','||PAR_GRUPO_E||','||PAR_TIPO_E||','||PAR_INV_E;
       INSERT INTO log_llantas
       VALUES (TO_NUMBER(SQ_LOG_LLANTAS.nextval),'ERROR EN PDB_RECIRCULAR'||' '||V2_OTRO_L,SYSDATE);
       par_retorno_s   := 'ERROR EN PDB_RECIRCULAR';    
  END;
  /*Función para retornar las profundidades de una ficha técnica de una llanta que este montada en un vehículo*/    
  FUNCTION FDB_PROFUNDIDAD_FICHA(PAR_LLANTA_E VARCHAR) RETURN SYS_REFCURSOR AS
  l_cursor sys_refcursor;
  BEGIN
  OPEN l_cursor FOR
    SELECT PI, PC, PD
    FROM FICHATEC,LLANTAS
    WHERE FICHA = CODIGO
    AND LLANTA = par_llanta_e;
  RETURN l_cursor;
  END;
  /*Función para retornar las profundidades de una ficha técnica de una llanta que este montada en un vehículo*/    
  FUNCTION FDB_DATOS_PLACA(PLACA VARCHAR) RETURN SYS_REFCURSOR AS
  placavalida number(1);
  l_cursor sys_refcursor;
  BEGIN
  select count(1) into placavalida 
  from vehiculos
  where vehi_placa_ch=PLACA;
  if(placavalida>0)then
    OPEN l_cursor FOR
      SELECT VEHI_PLACA_CH PLACA,
            VEHI_CLASE_NB CLASE,
            VEHI_MARCA_V2 MARCA,
            VEHI_MODELO_V2 MODELO,
            VEHI_CAPACIDAD_NB CAPACIDAD,
            VEHI_NOMOTOR_NB NOMOTOR,
            VEHI_NOEJES_NB NOEJES,
            VEHI_COLOR_V2 COLOR,
            VEHI_CHASIS_NB CHASIS,
            VEHI_CONSUMO_NB CONSUMO,
            VEHI_PROPIETARIO_NB PROPIETARIO,
            VEHI_NACION_NB NACION,
            VEHI_EMPAFIL_NB EMPAFIL,
            VEHI_ESTADO_NB ESTADO,
            VEHI_AFILIADO_NB AFILIADO,
            VEHI_VINCULA_NB VINCULA,
            VEHI_ESTADO_V2 ESTADO,
            VEHI_MODELOREPO_NB MODELOREPO,
            VEHI_LINEA_NB LINEA,
            VEHI_TIPOCARRO_NB TIPOCARRO,
            VEHI_NOSERIE_V2 NOSERIE,
            VEHI_CONFIGURACION_V2 CONFIGURACION,
            VEHI_PESOVACIO_NB PESOVACIO,
            VEHI_FECCREA_DT FECCREA,
            VEHI_USUCREA_NB USUCREA,
            VEHI_FECANULA_DT FECANULA,
            VEHI_USUANULA_NB USUANULA,
            VEHI_OFICREA_NB OFICREA,
            VEHI_OFIACTUALIZA_NB OFIACTUALIZA
      FROM VEHICULOS
      WHERE VEHI_PLACA_CH=PLACA;
  else
      /*select count(1) into placavalida 
      from trailers
      where trai_placa_ch=PLACA;
      if(placavalida>0)then*/
        OPEN l_cursor FOR
          SELECT TRAI_PLACA_CH PLACA,
                TRAI_TIPO_NB TIPO,
                TRAI_MARCA_V2 MARCA,
                TRAI_MODELO_NB MODELO,
                TRAI_SERIE_NB SERIE,
                TRAI_PROPIET_NB PROPIET,
                TRAI_CATEGORIA_NB CATEGORIA,
                TRAI_NACION_NB NACION,
                TRAI_NOEJES_NB NOEJES,
                TRAI_CAPACIDAD_NB CAPACIDAD,
                TRAI_TIPOPROPIETA_NB TIPOPROPIETA,
                TRAI_ESTADO_V2 ESTADO,
                TRAI_FECCREA_DT FECCREA,
                TRAI_USUCREA_NB USUCREA,
                TRAI_FECANULA_DT FECANULA,
                TRAI_USUANULA_NB USUANULA,
                TRAI_OFICREA_NB OFICREA,
                TRAI_OFIACTUALIZA_NB OFIACTUALIZA,
                TRAI_PESO_NB PESO,
                TRAI_CAMPO1_NB CAMPO1
          FROM TRAILERS
          WHERE TRAI_PLACA_CH=PLACA;
      /*END IF;*/
  end if;
RETURN l_cursor;
  END;
END;


