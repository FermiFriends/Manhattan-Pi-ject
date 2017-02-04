from setuptools import setup

setup(
    name='pynamite',
    packages=['pynamite'],
    include_package_data=True,
    install_requires=[
        'flask==0.12',
    ],
    setup_requires=[
        'pytest-runner',
    ],
    tests_require=[
        'pytest',
    ],
)
