# Notes

## Support for Double-precision Numbers in the Forth-like Language

Incorporating double-precision number support in our implementation would most likely involve the following changes:

- Enhance the stack: We'd need to update our data stack to be able to handle pairs of stack entries as a single
  double-precision number when appropriate.

- Update existing words: Some existing words would need to be updated or overloaded to handle double-precision numbers,
  such as words for arithmetic operations, comparison, and others.

- Introduce new double-precision-specific words: We need to incorporate the various double-precision words provided by
  Forth, like 2DROP, 2DUP, 2OVER, 2SWAP, D+ (double addition), D- (double subtraction), and others.

- Enhance the parser and compiler: Our parser and compiler need to be enhanced to discern and correctly handle
  double-precision literal numbers.
