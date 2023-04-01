import logging
from authlib.jose import JsonWebKey, RSAKey


def setup_jwk(app) -> JsonWebKey:
    app.private_key = RSAKey.generate_key(is_private=True)
    logging.info(str(app.private_key.get_public_key()))
    app.public_key = RSAKey.import_key(app.private_key.get_public_key())
    logging.info(str(app.public_key))