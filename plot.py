# python3 plot.py output/SingleThread_Benchmark.csv
# python3 plot.py output/MultiThread_Benchmark.csv

import sys
import pandas as pd
import matplotlib.pyplot as plt
import re

def extrair_valor_erro(valor_str):
    match = re.match(r'([\d\.]+)\+/-([\d\.]+)', valor_str)
    if match:
        return float(match.group(1)), float(match.group(2))
    else:
        return float(valor_str), 0.0

# Leitura do CSV
df_raw = pd.read_csv(sys.argv[1])

# Tirar espaços extras em colunas
df_raw.columns = df_raw.columns.str.strip()

# Extrair valores e erros
df_valores = pd.DataFrame()
df_erros = pd.DataFrame()
df_valores['n'] = df_raw['n']
df_erros['n'] = df_raw['n']

for col in df_raw.columns[1:]:
    vals = []
    errs = []
    for v in df_raw[col]:
        val, err = extrair_valor_erro(v)
        vals.append(val)
        errs.append(err)
    df_valores[col] = vals
    df_erros[col] = errs

# Identificar operações e tipos nas colunas
# Supondo que colunas tem formato "Tipo_Operação(sufixo opcional)"
# Exemplo: "Vector_add(0 e)", "ThreadSafe_contains(rand)"

operacoes = set()
tipos = set()
col_map = dict()

for col in df_valores.columns[1:]:  # pular 'n'
    # Quebrar por "_", ex: "Vector_add(0 e)"
    partes = col.split('_', 1)
    if len(partes) < 2:
        continue
    tipo = partes[0]
    resto = partes[1]

    # Operação = parte inicial do resto antes de qualquer caractere especial, tipo parênteses
    op_match = re.match(r'([a-zA-Z]+)', resto)
    if not op_match:
        continue
    op = op_match.group(1)

    operacoes.add(op)
    tipos.add(tipo)

    if op not in col_map:
        col_map[op] = {}
    col_map[op][tipo] = col

# Comparação entre operações
for op in operacoes:
    plt.figure(figsize=(8,5))
    for tipo in col_map[op]:
        col_name = col_map[op][tipo]
        plt.errorbar(df_valores['n'], df_valores[col_name], yerr=df_erros[col_name], 
                     fmt='o-', capsize=4, label=tipo)
    plt.xscale('log')
    plt.yscale('log')
    plt.xlabel('n (elementos na lista) (log scale)')
    plt.ylabel('tempo(ns) (log scale)')
    plt.title(f'Computação de List.{op}()')
    plt.legend()
    plt.grid(True, which="both", ls="--", linewidth=0.5)
    plt.tight_layout()
    plt.show()

    # Operações por segundo
    for tipo in col_map[op]:
        col_name = col_map[op][tipo]

        # valores de tempo médio e erro
        tempo = df_valores[col_name]
        erro_tempo = df_erros[col_name]

        # operações por segundo e erro propagado
        ops_por_segundo = 1e9 / tempo
        erro_ops = (erro_tempo / tempo) * ops_por_segundo

        plt.errorbar(df_valores['n'], ops_por_segundo, yerr=erro_ops,
                     fmt='o-', capsize=4, label=tipo)

    plt.xscale('log')
    plt.xlabel('n (elementos na lista) (log scale)')
    plt.ylabel('operações por segundo (ops/s)')
    plt.title(f'Desempenho de List.{op}()')
    plt.legend()
    plt.grid(True, which="both", ls="--", linewidth=0.5)
    plt.tight_layout()
    plt.show()