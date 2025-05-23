#!/usr/bin/env python3
import sys
import tiktoken

def main():
    if len(sys.argv) < 4:
        print("Uso: python count_tokens.py <modelo> <max_tokens> <texto>")
        sys.exit(1)

    model = sys.argv[1]
    try:
        max_tokens = int(sys.argv[2])
    except ValueError:
        print("max_tokens debe ser un número entero")
        sys.exit(1)

    text = sys.argv[3]

    encoding = tiktoken.encoding_for_model(model)
    tokens = encoding.encode(text)

    print(len(tokens))  # número de tokens totales

    truncated_tokens = tokens[:max_tokens]
    truncated_text = encoding.decode(truncated_tokens)
    print(truncated_text)

if __name__ == "__main__":
    main()
